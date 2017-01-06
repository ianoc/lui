scala_library(
    name = "hadoop",
    srcs = glob(["**/*.scala"]),
    deps = [
        "//3rdparty/jvm/org/apache/parquet:parquet_format",
        "//3rdparty/jvm/org/apache/parquet:parquet_hadoop",
        "//3rdparty/jvm/org/apache/parquet:parquet_encoding",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_client",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_yarn_api",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_core",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_jobclient",
        "//src/scala/com/twitter/lui/core/parquet",
        "//src/scala/org/apache/parquet",
        "//3rdparty/jvm/commons_logging",
    ],
    exports = [
        "//3rdparty/jvm/org/apache/parquet:parquet_format",
        "//3rdparty/jvm/org/apache/parquet:parquet_hadoop",
        "//3rdparty/jvm/org/apache/parquet:parquet_encoding",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_client",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_core",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_jobclient",
        "//src/scala/com/twitter/lui/core/parquet",
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
