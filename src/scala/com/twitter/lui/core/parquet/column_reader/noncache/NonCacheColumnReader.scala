/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.twitter.lui.column_reader.noncache

import org.apache.parquet.Log
import org.apache.parquet.VersionParser.ParsedVersion
import org.apache.parquet.column.ColumnDescriptor
import org.apache.parquet.column.page.PageReader

import com.twitter.lui.column_reader._
object NonCacheColumnReader {
  private val LOG: Log = Log.getLog(getClass)
}

abstract class NonCacheColumnReader(writerVersion: ParsedVersion,
                                    path: ColumnDescriptor,
                                    pageReader: PageReader,
                                    maxDefinitionLevel: Int,
                                    maxRepetitionLevel: Int)
    extends BaseColumnReader(writerVersion,
                             path,
                             pageReader,
                             maxDefinitionLevel,
                             maxRepetitionLevel)
    with SpecificTypeGet {

  require(maxRepetitionLevel == 0,
          "The non cache class can only be used where we have no repetition's occuring")

  def advanceSetRecord(newRecordPosition: Long): Unit = {
    if (newRecordPosition != recordPosition) {
      while (recordPosition < newRecordPosition) {
        skip()
      }
    }
  }

  def primitiveAdvance(newRecordPosition: Long, targetOffset: Array[Int]): Boolean = {
    while (recordPosition < newRecordPosition) {
      skip()
    }
    val delta = newRecordPosition - recordPosition
    if (delta < -1)
      sys.error(
        s"$columnName: \t Attempted to access an old field, trying to goto $newRecordPosition while on $recordPosition")
    if (delta < 0) false
    else {
      // Now we have gotten to the best place we can, is it null?
      getCurrentDefinitionLevel == maxDefinitionLevel
    }
  }

}
