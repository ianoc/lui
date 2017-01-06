#!/bin/bash

# docker can't use sandboxing, standalone disables
set -e
cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ../../ # Was in ci folder

echo "Running formatting tests"
./scripts/format_scala.sh -t
./scripts/format_build_files.sh -t

echo "Validating all targets in build"
./scripts/ci/validate_all_targets_in_builds.sh

echo "Running Bazel tests"
# Since output_base is changed to /cache, the junit outputs live there too; we
# have to make sure to clear them, or we might see test results from a previous
# run.
rm -rf $(bazel info bazel-testlogs)
bazel test --verbose_failures --test_output=errors //test/...

echo "Junit moving for jenkins to pick up"
./scripts/ci/junit.sh

echo "Compiling all targets"
bazel build --verbose_failures //src/...

echo "Building deploys"
#/data-common/scripts/build_deploys.sh /build

echo "Ensuring no external targets in src or test"
if grep -r "//external:" src test
  then echo "//external deps should be in 3rdparty" && grep -r "//external:" src test && false
  else true;
fi
