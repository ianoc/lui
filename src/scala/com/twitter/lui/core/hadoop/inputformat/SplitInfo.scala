package com.twitter.lui.inputformat

import java.util.ArrayList

import java.util.{List => JList}

import org.apache.hadoop.fs.BlockLocation
import org.apache.hadoop.fs.FileStatus

import org.apache.parquet.hadoop.metadata.BlockMetaData
import org.apache.parquet.schema.MessageType
import org.apache.parquet.schema.MessageTypeParser
import org.apache.parquet.hadoop.ParquetInputSplit
import scala.collection.JavaConverters._

private[inputformat] class SplitInfo(hdfsBlock: BlockLocation) {
  private[this] val rowGroups = new ArrayList[BlockMetaData]()
  var compressedByteSize: Long = 0L

  def addRowGroup(rowGroup: BlockMetaData): Unit = {
    rowGroups.add(rowGroup)
    compressedByteSize += rowGroup.getCompressedSize()
  }

  def getCompressedByteSize: Long = compressedByteSize

  def getRowGroups: JList[BlockMetaData] = rowGroups

  def getRowGroupCount: Int = rowGroups.size()

  def getParquetInputSplit(fileStatus: FileStatus, requestedSchema: String): ParquetInputSplit = {

    val requested: MessageType = MessageTypeParser.parseMessageType(requestedSchema)

    val length: Long = getRowGroups.asScala.flatMap { block: BlockMetaData =>
      block.getColumns.asScala.map { column =>
        if (requested.containsPath(column.getPath.toArray())) {
          column.getTotalSize
        } else 0L
      }
    }.sum

    val lastRowGroup: BlockMetaData = getRowGroups.get(this.getRowGroupCount - 1)
    val end: Long = lastRowGroup.getStartingPos + lastRowGroup.getTotalByteSize

    val rowGroupOffsets = rowGroups.asScala.map(_.getStartingPos).toArray

    new ParquetInputSplit(fileStatus.getPath,
                          hdfsBlock.getOffset,
                          end,
                          length,
                          hdfsBlock.getHosts,
                          rowGroupOffsets)
  }
}
