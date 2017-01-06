#!/bin/bash

echo -ne "\033[0;32m"
echo 'Updating bazel dependencies. This will take about five minutes.'
echo -ne "\033[0m"

# update this to move to later versions of this repo:
# https://github.com/johnynek/bazel-deps
GITSHA="cae5f9f2721f03ed12d23c28959792ae94780c53"

set -e

SCRIPTS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $SCRIPTS_DIR

REPO_ROOT=$(git rev-parse --show-toplevel)

BAZEL_DEPS_PATH="$HOME/.bazel-deps-cache/$(basename $REPO_ROOT)"
BAZEL_DEPS_REPO_PATH="$BAZEL_DEPS_PATH/bazel-deps"
BAZEL_DEPS_WORKSPACE="$BAZEL_DEPS_REPO_PATH/WORKSPACE"

if [ ! -f "$BAZEL_DEPS_WORKSPACE" ]; then
  mkdir -p $BAZEL_DEPS_PATH
  cd $BAZEL_DEPS_PATH
  git clone https://github.com/johnynek/bazel-deps.git
fi

cd $BAZEL_DEPS_REPO_PATH
git reset --hard $GITSHA || (git fetch && git reset --hard $GITSHA)
$REPO_ROOT/bazel --batch build src/scala/com/github/johnynek/bazel_deps/parseproject_deploy.jar

cd $REPO_ROOT
$BAZEL_DEPS_REPO_PATH/gen_maven_deps.sh `pwd` 3rdparty/workspace.bzl maven_dependencies.yaml
./scripts/format_build_files.sh 2>&1 | grep -v fixed || true
