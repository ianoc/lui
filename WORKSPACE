workspace(name = "net_ianoc_lui")

BAZEL_VERSION = "1f49b6befa57e5cea819d5010785a90fc5bd0db6"

git_repository(
    name = "io_bazel_rules_scala",
    remote = "git://github.com/bazelbuild/rules_scala",
    commit = "139fcc7937e56d13b4dda476b442af8ee5d530a1"
)

git_repository(
    name = "com_github_nelhage_bazel_git_repositories",
    remote = "git://github.com/nelhage/bazel_git_repositories",
    commit = "0087c0fe6623c59dde878e94dfd2caa8af9325e2",
)

load("@com_github_nelhage_bazel_git_repositories//:repositories.bzl",
     "new_native_git_repository",
     "native_git_repository")

native_git_repository(
    name = "io_bazel",
    remote = "git://github.com/bazelbuild/bazel.git",
    commit = BAZEL_VERSION,
)

# we are replacing the standard compiler with one that can suppress stub warnings:
# scala_repositories()

http_file(
    name = "scalatest",
    url = "https://oss.sonatype.org/content/groups/public/org/scalatest/scalatest_2.11/2.2.6/scalatest_2.11-2.2.6.jar",
    sha256 = "f198967436a5e7a69cfd182902adcfbcb9f2e41b349e1a5c8881a2407f615962",
)

load("@io_bazel_rules_scala//twitter_scrooge:twitter_scrooge.bzl", "twitter_scrooge")
twitter_scrooge()
# Override the runtime dependencies to match those in the repo
bind(name = 'io_bazel_rules_scala/dependency/thrift/libthrift', actual = '//3rdparty/jvm/org/apache/thrift:libthrift')
bind(name = 'io_bazel_rules_scala/dependency/thrift/scrooge_core', actual = '//3rdparty/jvm/com/twitter:scrooge_core')

load("//3rdparty:workspace.bzl", "maven_dependencies")
load("//3rdparty:maven_load.bzl", "maven_load")
maven_dependencies(maven_load)
