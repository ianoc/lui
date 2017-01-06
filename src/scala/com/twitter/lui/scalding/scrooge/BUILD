scala_library(
    name = "scrooge",
    srcs = glob(["**/*.scala"]),
    deps = [
        "//3rdparty/jvm/com/twitter:scalding_core",
        "//3rdparty/jvm/cascading:cascading_core",
        "//src/scala/com/twitter/lui/scrooge",
        "//3rdparty/jvm/com/twitter:scrooge_core",
        "//3rdparty/jvm/org/apache/thrift:libthrift",
        "//src/scala/com/twitter/lui/core/hadoop",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_core",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_common",
        "//3rdparty/jvm/org/apache/hadoop:hadoop_mapreduce_client_jobclient",
        "//3rdparty/jvm/com/twitter/elephantbird:elephant_bird_cascading2",
        "//3rdparty/jvm/com/twitter/elephantbird:elephant_bird_core",
    ],
    exports = [
        "//3rdparty/jvm/org/apache/hadoop:hadoop_common",
    ],
    visibility = ["//visibility:public"],
)
