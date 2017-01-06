scala_library(
    name = "parquet",
    srcs = glob(["**/*.scala"]),
    deps = [
        "//3rdparty/jvm/org/apache/parquet:parquet_common",
        "//3rdparty/jvm/org/apache/parquet:parquet_column",
        "//3rdparty/jvm/org/apache/parquet:parquet_hadoop",
        "//3rdparty/jvm/org/apache/parquet:parquet_encoding",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_client",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_common",
        "//src/scala/org/apache/parquet",
    ],
    exports = [
        "//3rdparty/jvm/org/apache/parquet:parquet_common",
        "//3rdparty/jvm/org/apache/parquet:parquet_column",
        "//3rdparty/jvm/org/apache/parquet:parquet_hadoop",
        "//3rdparty/jvm/org/apache/parquet:parquet_encoding",
        "//src/scala/org/apache/parquet",
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
