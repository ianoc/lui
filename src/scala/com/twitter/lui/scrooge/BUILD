scala_library(
    name = "scrooge",
    srcs = glob(["**/*.scala"]),
    deps = [
        "//3rdparty/jvm/com/twitter:algebird_core",
        "//3rdparty/jvm/com/twitter:scrooge_core",
        "//3rdparty/jvm/org/apache/thrift:libthrift",
        "//3rdparty/jvm/org/apache/parquet:parquet_jackson",
        "//3rdparty/jvm/org/apache/parquet:parquet_thrift",
        "//3rdparty/jvm/org/apache/parquet:parquet_common",
        "//3rdparty/jvm/org/apache/parquet:parquet_column",
        "//3rdparty/jvm/org/apache/parquet:parquet_hadoop",
        "//3rdparty/jvm/org/apache/parquet:parquet_encoding",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_client",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_common",
        "//src/scala/org/apache/parquet",
        "//src/scala/com/twitter/lui/core/hadoop",
        "//3rdparty/jvm/org/scala_lang:scala_reflect",
        "//3rdparty/jvm/org/scala_lang:scala_compiler",
    ],
    exports = [
    ],
    visibility = ["//visibility:public"],
    scalacopts = [
        "-Ywarn-dead-code",
        "-Ywarn-unused-import",  # macros can cause this to kill the compiler, see: https://github.com/scala/pickling/issues/370
        "-Ywarn-value-discard",
        "-Xmax-classfile-name", "128",  # Linux laptops don't like long file names
        "-Xlint",
        "-Xfuture",
        "-deprecation",
        # "-Xfatal-warnings", this hits https://issues.scala-lang.org/browse/SI-9673 on stubb
    ],
)
