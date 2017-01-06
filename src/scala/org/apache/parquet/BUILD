scala_library(
    name = "parquet",
    srcs = glob(["**/*.scala"]),
    deps = [
        "//3rdparty/jvm/org/apache/parquet:parquet_common",
        "//3rdparty/jvm/org/apache/parquet:parquet_format",
        "//3rdparty/jvm/org/apache/parquet:parquet_column",
        "//3rdparty/jvm/org/apache/parquet:parquet_hadoop",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_client",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_core",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_jobclient",
    ],
    visibility = ["//visibility:public"],
)
