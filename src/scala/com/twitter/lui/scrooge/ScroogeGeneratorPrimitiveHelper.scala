package com.twitter.lui.scrooge

import com.twitter.scrooge.ThriftStructFieldInfo

object ScroogeGeneratorPrimitiveHelper {
  import ScroogeGenerator._

  def nonMethod(f: ThriftStructFieldInfo,
                existingPath: ExistingPath,
                mapData: Map[String,
                             (ParquetFieldInfo,
                              Option[Int],
                              Option[ParquetThriftEnumInfo],
                              Vector[Short])]): (String, String) = {

    val path = if (existingPath.toStr.isEmpty) f.id.toString else s"${existingPath.toStr}/${f.id}"
    val primitiveZero = bytePrimtiveZero(f)
    val getter = byteToJavaGetter(f)

    mapData
      .get(path)
      .map {
        case (parquetFieldInfo, Some(colIdx), enumInfoOpti, _) =>
          val setString =
            if (f.isOptional)
              s"""if(isNull) {
              None
            } else {
              Some($getter)
            }
            """
            else
              s"if(isNull) $primitiveZero else $getter"

          ("",
           s"""{
              val rdr = colReaders($colIdx)
              val isNull: Boolean = !rdr.primitiveAdvance(recordIdx, repetitionOffsets)
              $setString
            }
          """)
        case args @ _ => sys.error(s"Invalid args: $args")
      }
      .getOrElse(("", if (f.isOptional) "None" else primitiveZero))
  }

  def apply(f: ThriftStructFieldInfo,
            existingPath: ExistingPath,
            mapData: Map[String,
                         (ParquetFieldInfo,
                          Option[Int],
                          Option[ParquetThriftEnumInfo],
                          Vector[Short])]): (String, String) = {
    val tpeString = byteToJavaPrimitive(f)

    val (objectSetup, innerBuilder) = nonMethod(f, existingPath, mapData)

    ("",
     s"""
          lazy val `${f.name}`: $tpeString = $innerBuilder
          """)
  }
}
