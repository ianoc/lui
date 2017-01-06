package com.twitter.lui.scrooge

import com.twitter.scrooge.ThriftUnion
import com.twitter.scrooge.ThriftStructFieldInfo

object ScroogeGeneratorStructHelper {
  import ScroogeGenerator._

  def nonMethod(f: ThriftStructFieldInfo,
                existingPath: ExistingPath,
                mapData: Map[String,
                             (ParquetFieldInfo,
                              Option[Int],
                              Option[ParquetThriftEnumInfo],
                              Vector[Short])]): (String, String) = {
    val path = if (existingPath.toStr.isEmpty) f.id.toString else s"${existingPath.toStr}/${f.id}"
    val tpeString = byteToJavaPrimitive(f) // Can include things like Option[String]
    val tpeStringNoOption = byteToJavaPrimitive(f, false) // in the example above would be String instead
    val nullV = if (f.isOptional) "None" else "null"

    mapData
      .get(path)
      .map {
        case (locFieldInfo, _, _, pathV) =>
          val fieldRepetitionLevel = locFieldInfo.maxRepetitionLevel
          val fieldDefinitionLevel = locFieldInfo.maxDefinitionLevel
          val (_, Some(fieldClosestPrimitiveColumnIdx), _, _) =
            mapData(locFieldInfo.closestPrimitiveChild.get.toVector.mkString("/"))

          val buildType =
            if (classOf[ThriftUnion].isAssignableFrom(Class.forName(tpeStringNoOption))) "Union"
            else "Struct"

          val innerBuilder =
            s"""scroogeGenerator.build${buildType}(classOf[$tpeStringNoOption], $tpeStringNoOption.fieldInfos , "${pathV.mkString(
              "/")}", colReaders, recordIdx, repetitionOffsets).asInstanceOf[$tpeStringNoOption]"""

          val someV =
            if (f.isOptional)
              s"Some($innerBuilder)"
            else
              innerBuilder

          val notNullCall =
            if (fieldRepetitionLevel > 0)
              s"""notNull($fieldDefinitionLevel,
              recordIdx,
              Array[Int](0),
              Array[Int](${(0 until fieldRepetitionLevel + 2).map(_ => 0).mkString(",")}),
              repetitionOffsets)"""
            else
              s"notNull($fieldDefinitionLevel, recordIdx)"

          ("",
           s"""{
          val primitive = colReaders($fieldClosestPrimitiveColumnIdx).asInstanceOf[CacheColumnReader]
          primitive.advanceSetRecord(recordIdx)

          if(primitive.$notNullCall) {
              $someV
            } else {
              $nullV
            }
          }
          """)
      }
      .getOrElse {
        ("", s"$nullV")
      }
  }

  def apply(f: ThriftStructFieldInfo,
            existingPath: ExistingPath,
            mapData: Map[String,
                         (ParquetFieldInfo,
                          Option[Int],
                          Option[ParquetThriftEnumInfo],
                          Vector[Short])]): (String, String) = {

    val (objectCode, innerGetCode) = nonMethod(f, existingPath, mapData)
    val tpeString = byteToJavaPrimitive(f) // Can include things like Option[String]

    (objectCode,
     s"""
          lazy val ${f.name}: $tpeString = $innerGetCode
          """)
  }
}
