
py_library(
    name = "shared_utilities",
    srcs = ["shared_utilities.py"],
    visibility = ["//visibility:public"]
)

py_binary(
    name = "build_deploy_zip",
    srcs = ["build_deploy_zip.py"],
    deps = [
        ":shared_utilities"
    ],
    visibility = ["//visibility:public"]
)
