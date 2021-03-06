package com.twitter.lui.inputformat

import java.util.ArrayList
import java.util.{List => JList}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.BlockLocation
import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path

import org.apache.parquet.Log
import com.twitter.lui.hadoop.ReadContext
import org.apache.parquet.hadoop.metadata.BlockMetaData
import org.apache.parquet.hadoop.metadata.ParquetMetadata
import org.apache.parquet.io.ParquetDecodingException
import org.apache.parquet.hadoop.ParquetInputSplit
import scala.collection.JavaConverters._
import org.apache.parquet.hadoop.Footer

object ClientSideMetadataSplitStrategy {

  /**
   * groups together all the data blocks for the same HDFS block
   *
   * @param rowGroupBlocks      data blocks (row groups)
   * @param hdfsBlocksArray     hdfs blocks
   * @param fileStatus          the containing file
   * @param requestedSchema     the schema requested by the user
   * @param readSupportMetadata the metadata provided by the readSupport implementation in init
   * @param minSplitSize        the mapred.min.split.size
   * @param maxSplitSize        the mapred.max.split.size
   * @return the splits (one per HDFS block)
   * @throws IOException If hosts can't be retrieved for the HDFS block
   */
  def generateSplits[T](rowGroupBlocks: JList[BlockMetaData],
                        hdfsBlocksArray: Array[BlockLocation],
                        fileStatus: FileStatus,
                        requestedSchema: String,
                        minSplitSize: Long,
                        maxSplitSize: Long): JList[ParquetInputSplit] = {

    val splitRowGroups: JList[SplitInfo] =
      generateSplitInfo(rowGroupBlocks, hdfsBlocksArray, minSplitSize, maxSplitSize)

    //generate splits from rowGroups of each split
    val resultSplits: JList[ParquetInputSplit] = new ArrayList[ParquetInputSplit]()
    splitRowGroups.asScala.foreach { splitInfo =>
      val split: ParquetInputSplit =
        splitInfo.getParquetInputSplit(fileStatus, requestedSchema)
      resultSplits.add(split)
    }
    resultSplits
  }

  def generateSplitInfo(rowGroupBlocks: JList[BlockMetaData],
                        hdfsBlocksArray: Array[BlockLocation],
                        minSplitSize: Long,
                        maxSplitSize: Long): JList[SplitInfo] = {

    if (maxSplitSize < minSplitSize || maxSplitSize < 0 || minSplitSize < 0) {
      throw new ParquetDecodingException(
        "maxSplitSize and minSplitSize should be positive and max should be greater or equal to the minSplitSize: maxSplitSize = " + maxSplitSize + "; minSplitSize is " + minSplitSize)
    }
    val hdfsBlocks: HDFSBlocks = HDFSBlocks(hdfsBlocksArray)
    hdfsBlocks.checkBelongingToANewHDFSBlock(rowGroupBlocks.get(0))
    var currentSplit: SplitInfo = new SplitInfo(hdfsBlocks.getCurrentBlock)

    //assign rowGroups to splits
    var splitRowGroups: JList[SplitInfo] = new ArrayList[SplitInfo]()
    checkSorted(rowGroupBlocks); //assert row groups are sorted
    rowGroupBlocks.asScala.foreach { rowGroupMetadata =>
      if ((hdfsBlocks.checkBelongingToANewHDFSBlock(rowGroupMetadata)
          && currentSplit.getCompressedByteSize >= minSplitSize
          && currentSplit.getCompressedByteSize > 0)
          || currentSplit.getCompressedByteSize >= maxSplitSize) {
        //create a new split
        splitRowGroups.add(currentSplit); //finish previous split
        currentSplit = new SplitInfo(hdfsBlocks.getCurrentBlock)
      }
      currentSplit.addRowGroup(rowGroupMetadata)
    }

    if (currentSplit.getRowGroupCount > 0) {
      splitRowGroups.add(currentSplit)
    }

    splitRowGroups
  }

  private def checkSorted(rowGroupBlocks: JList[BlockMetaData]): Unit = {
    var previousOffset = 0L
    rowGroupBlocks.asScala.foreach { rowGroup =>
      val currentOffset: Long = rowGroup.getStartingPos
      if (currentOffset < previousOffset) {
        throw new ParquetDecodingException(
          "row groups are not sorted: previous row groups starts at " + previousOffset + ", current row group starts at " + currentOffset)
      }
    }
  }

  private val LOG: Log = Log.getLog(getClass)

}
class ClientSideMetadataSplitStrategy {
  import ClientSideMetadataSplitStrategy._

  def getSplits(configuration: Configuration,
                footers: JList[Footer],
                maxSplitSize: Long,
                minSplitSize: Long,
                readContext: ReadContext): JList[ParquetInputSplit] = {
    val splits: JList[ParquetInputSplit] = new ArrayList[ParquetInputSplit]()

    var totalRowGroups: Long = 0L

    footers.asScala.foreach { footer =>
      val file: Path = footer.getFile
      LOG.debug(file)
      val fs: FileSystem = file.getFileSystem(configuration)
      val fileStatus: FileStatus = fs.getFileStatus(file)
      val parquetMetaData: ParquetMetadata = footer.getParquetMetadata()
      val blocks: JList[BlockMetaData] = parquetMetaData.getBlocks()

      totalRowGroups += blocks.size()

      val fileBlockLocations: Array[BlockLocation] =
        fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen())
      splits.addAll(
        generateSplits(blocks,
                       fileBlockLocations,
                       fileStatus,
                       readContext.requestedSchema.toString(),
                       minSplitSize,
                       maxSplitSize))
    }

    splits
  }

}
