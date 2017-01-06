package com.twitter.lui.scalding.scrooge

import cascading.scheme.Scheme
import com.twitter.scalding.{FileSource, SingleMappable, LocalTapSource, HadoopSchemeInstance, FixedPathSource, Config, TypedPipe}
import com.twitter.scrooge.ThriftStruct
import org.apache.hadoop.mapred.{JobConf, OutputCollector, RecordReader}
import scala.reflect.ClassTag
import com.twitter.scalding.HfsConfPropertySetter

trait LuiScroogeSource[T >: Null <: ThriftStruct]
    extends FileSource
    with SingleMappable[T]
    with LocalTapSource {
  def ct: ClassTag[T]

  override def hdfsScheme: Scheme[JobConf, RecordReader[_, _], OutputCollector[_, _], _, _] = {
    val scheme = new LuiScroogeScheme[T]()(ct)
    HadoopSchemeInstance(scheme.asInstanceOf[Scheme[_, _, _, _, _]])
  }
}

case class FPLuiScroogeSource[T >: Null <: ThriftStruct: ClassTag](splitSize: Option[Long])(paths: String*)
    extends FixedPathSource(paths: _*)
    with LuiScroogeSource[T]
    with HfsConfPropertySetter {

  override def writeTapConfig = {
    Config.empty.+(("mapreduce.fileoutputcommitter.algorithm.version", "2"))
  }


  override def readTapConfig = {
    val baseCfg = Config.empty
    splitSize match {
      case None => baseCfg
      case Some(v) =>
        baseCfg
          .+(("mapreduce.input.fileinputformat.split.minsize", v.toString))
          .+(("mapreduce.input.fileinputformat.split.maxsize", v.toString))
    }
  }

  override def ct = implicitly[ClassTag[T]]
}


object FPLuiScroogeSource {
  def collect[T >: Null <: ThriftStruct: ClassTag, U](splitSize: Option[Long])(paths: String*)(pf: PartialFunction[T, U]) =
    TypedPipe.from(new FPLuiScroogeSource[T](splitSize)(paths :_*)).collect(pf)

  def optionMap[T >: Null <: ThriftStruct: ClassTag, U](splitSize: Option[Long])(paths: String*)(fn: T => Option[U]) =
    collect[T,U](splitSize)(paths:_*){ case e if fn(e).isDefined => fn(e).get}
}
