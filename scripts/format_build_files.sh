#!/bin/bash

BIN="autopep8"
CMD="autopep8 -i"
if [ "$1" == "-t" ]; then
  BIN="pep8"
  CMD="pep8"
fi

WHICH=$(which $BIN)
if [ ! -x "$WHICH" ]; then
  echo "Couldn't find $BIN on your path. Please run 'pip install pep8 autopep8'"
  exit
fi

set -e

CMD="$CMD --ignore E251,E501,E711"
exec find . -name *.bzl -not -path "./bazel-*" | xargs $CMD
exec find . -name BUILD -not -path "./bazel-*" | xargs $CMD
$CMD WORKSPACE
