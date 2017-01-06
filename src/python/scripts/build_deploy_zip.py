import contextlib
import json
import os
import os.path
import re
import sys
import time
import zipfile

def get_args():
  with open(sys.argv[1]) as f:
      return json.load(f)

ignored = []

def add_to_classpath(jar):
  return jar and (len(filter(jar.startswith, ignored)) == 0)

def get_main_class(jar):
  f = zipfile.ZipFile(jar, 'r')
  line = f.read("META-INF/MANIFEST.MF")
  m = re.search("Main-Class: (.*)\r", line)
  if m:
      return m.group(1)

def build_launcher_script(main_jar, jars):
  return "\n".join(
    [ "#!/bin/bash"
    , "SCRIPTS_DIR=\"$( cd \"$( dirname \"${BASH_SOURCE[0]}\" )\" && pwd )\""
    , "cd $SCRIPTS_DIR"
    , "java -cp {cp} {main_class} \"$@\"".format(
       cp = ":".join(jars),
       main_class = get_main_class(main_jar))
    , ""
    ])

if __name__ == "__main__":
  args = get_args()
  with contextlib.closing(zipfile.ZipFile(args["output"], 'w')) as zipout:
      jars = filter(add_to_classpath, args["classpath"])
      script = build_launcher_script(args["main_jar"], jars)
      [zipout.write(j) for j in jars]

      launch_name = "launch.sh"
      launch_info = zipfile.ZipInfo(filename=launch_name, date_time=time.localtime(time.time())[:6])
      launch_info.external_attr = 0775 << 16L
      zipout.writestr(launch_info, script)
