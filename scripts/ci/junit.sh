#!/bin/bash

set -e
cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ../../ # Was in ci folder


junitdir="/log/junit"
mkdir -p $junitdir

COUNTER=0

for i in `find -L ./bazel-testlogs -iname "*.xml" | sort`; do
  cp "$i" "$junitdir/$COUNTER.xml"
  let COUNTER=COUNTER+1
done
