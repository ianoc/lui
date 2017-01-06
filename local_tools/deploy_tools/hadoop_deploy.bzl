load("//local_tools/deploy_tools:deploy.bzl", "deploy_zip_impl")

hadoop_deploy_zip = rule(
    implementation = deploy_zip_impl,
    attrs = {
        "scala_binary": attr.label(executable=True),
        "_deployer": attr.label(default=Label("//src/python/hadoop_scripts:build_deploy_zip"), executable=True)
    },
    outputs = {
        "zip": "%{name}.zip"
    })
