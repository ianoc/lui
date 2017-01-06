package com.twitter.lui.inputformat

import java.util.Arrays
import java.util.Comparator

import org.apache.hadoop.fs.BlockLocation

import org.apache.parquet.hadoop.metadata.BlockMetaData
import org.apache.parquet.io.ParquetDecodingException

//Wrapper of hdfs blocks, keep track of which HDFS block is being used

private[inputformat] object HDFSBlocks {

  def apply(hdfsBlocks: Array[BlockLocation]): HDFSBlocks = {
    val comparator = new Comparator[BlockLocation]() {
      override def compare(b1: BlockLocation, b2: BlockLocation): Int =
        java.lang.Long.signum(b1.getOffset() - b2.getOffset())
    }
    Arrays.sort(hdfsBlocks, comparator)
    new HDFSBlocks(hdfsBlocks)
  }
}

private[inputformat] class HDFSBlocks private (hdfsBlocks: Array[BlockLocation]) {
  var currentStartHdfsBlockIndex: Int = 0; //the hdfs block index corresponding to the start of a row group
  var currentMidPointHDFSBlockIndex: Int = 0; // the hdfs block index corresponding to the mid-point of a row group, a split might be created only when the midpoint of the rowgroup enters a new hdfs block

  private[this] def getHDFSBlockEndingPosition(hdfsBlockIndex: Int): Long = {
    val hdfsBlock = hdfsBlocks(hdfsBlockIndex)
    hdfsBlock.getOffset + hdfsBlock.getLength - 1
  }

  /**
   * @param rowGroupMetadata
   * @return true if the mid point of row group is in a new hdfs block, and also move the currentHDFSBlock pointer to the correct index that contains the row group
   * return false if the mid point of row group is in the same hdfs block
   */
  def checkBelongingToANewHDFSBlock(rowGroupMetadata: BlockMetaData): Boolean = {
    var isNewHdfsBlock: Boolean = false
    val rowGroupMidPoint: Long = rowGroupMetadata
        .getStartingPos() + (rowGroupMetadata.getCompressedSize() / 2)

    //if mid point is not in the current HDFS block any more, return true
    while (rowGroupMidPoint > getHDFSBlockEndingPosition(currentMidPointHDFSBlockIndex)) {
      isNewHdfsBlock = true
      currentMidPointHDFSBlockIndex += 1
      if (currentMidPointHDFSBlockIndex >= hdfsBlocks.length)
        throw new ParquetDecodingException(
          "the row group is not in hdfs blocks in the file: midpoint of row groups is "
            + rowGroupMidPoint
            + ", the end of the hdfs block is "
            + getHDFSBlockEndingPosition(currentMidPointHDFSBlockIndex - 1))
    }

    while (rowGroupMetadata.getStartingPos() > getHDFSBlockEndingPosition(
             currentStartHdfsBlockIndex)) {
      currentStartHdfsBlockIndex += 1
      if (currentStartHdfsBlockIndex >= hdfsBlocks.length)
        throw new ParquetDecodingException(
          "The row group does not start in this file: row group offset is "
            + rowGroupMetadata.getStartingPos()
            + " but the end of hdfs blocks of file is "
            + getHDFSBlockEndingPosition(currentStartHdfsBlockIndex))
    }
    isNewHdfsBlock
  }

  def getCurrentBlock: BlockLocation = hdfsBlocks(currentStartHdfsBlockIndex)
}
