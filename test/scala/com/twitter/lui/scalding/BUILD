scala_test(
    name = "scalding",
    srcs = [
        "IntegrationTests.scala",
    ],
    size = "large",
    deps = [
        "//3rdparty/jvm/com/twitter:scalding_core",
        "//3rdparty/jvm/com/twitter:scalding_parquet_scrooge",
        "//3rdparty/jvm/com/twitter:scalding_parquet",
        "//3rdparty/jvm/org/scalacheck",
        "//src/thrift/com/twitter/thrift/lui/fixtures",
        "//src/scala/com/twitter/lui/scalding/scrooge",
        "//3rdparty/jvm/org/apache/parquet:parquet_thrift",
        "//3rdparty/jvm/org/apache/parquet:parquet_hadoop",
    ]
)
