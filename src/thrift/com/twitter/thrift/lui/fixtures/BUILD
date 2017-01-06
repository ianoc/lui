thrift_library(
    name = "fixtures_thrift",
    srcs = [
        "Test.thrift",
    ],
    visibility = ["//visibility:public"],
)


scrooge_scala_library(
    name = "fixtures",
    deps = [
        ":fixtures_thrift",
    ],
    visibility = ["//visibility:public"],
)
