# This build the binary target and then packages it up as a zip file
def deploy_zip_impl(ctx):
    if not hasattr(ctx.attr.scala_binary, "scala"):
        fail("this rule expects a scala_binary")

    rtdeps = ctx.attr.scala_binary.scala.transitive_runtime_deps
    rtdeps_path = [f.path for f in rtdeps]
    main_jar = ctx.attr.scala_binary.scala.outputs.class_jar
    main_jar_path = main_jar.path
    jars = struct(
        main_jar = main_jar_path,
        classpath = rtdeps_path,
        output = ctx.outputs.zip.path)
    jars_json = ctx.new_file("{}_jars.json".format(ctx.label.name))
    ctx.file_action(output=jars_json, content=jars.to_json())
    ctx.action(inputs = [main_jar, jars_json] + list(rtdeps),
               outputs = [ctx.outputs.zip],
               progress_message="build deploy zip",
               executable = ctx.executable._deployer,
               arguments = [jars_json.path])

deploy_zip = rule(
    implementation = deploy_zip_impl,
    attrs = {
        "scala_binary": attr.label(executable=True),
        "_deployer": attr.label(default=Label("//src/python/scripts:build_deploy_zip"), executable=True)
    },
    outputs = {
        "zip": "%{name}.zip"
    })
