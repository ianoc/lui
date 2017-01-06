#!/bin/bash
set -e
cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ../../ # Was in ci folder

TARGET_CONTENTS_OUT=$(mktemp)
bazel query 'labels("srcs", test/scala/...)' > $TARGET_CONTENTS_OUT
bazel query 'labels("srcs", src/scala/...)' >> $TARGET_CONTENTS_OUT
bazel query 'labels("srcs", src/java/...)' >> $TARGET_CONTENTS_OUT


TARGET_CONTENTS_SORTED_OUT=$(mktemp)
cat $TARGET_CONTENTS_OUT | sed -e 's/\/\///g' | sed -e 's/:/\//g' | sort > $TARGET_CONTENTS_SORTED_OUT

FOUND_SORTED_OUT=$(mktemp)
FOUND_CONTENTS_OUT=$(mktemp)

find test/scala src/scala -name '*.scala' > $FOUND_CONTENTS_OUT
find src/java -name '*.java' >> $FOUND_CONTENTS_OUT
cat $FOUND_CONTENTS_OUT | sort > $FOUND_SORTED_OUT

set +e
DIFF_CONTENTS=$(diff $TARGET_CONTENTS_SORTED_OUT $FOUND_SORTED_OUT)
set -e

if [ "$DIFF_CONTENTS" == "" ]; then
  exit 0;
else
  echo "Diff not empty"
  echo "Some targets are not in builds!"
  diff $TARGET_CONTENTS_SORTED_OUT $FOUND_SORTED_OUT
  exit 1;
fi
