import os
import os.path
import sys
import zipfile
import contextlib
import zlib

def build_duplicate_classes(jars):
  res = {}
  for jar in jars:
    if jar.endswith(".jar"):
      zip_file = zipfile.ZipFile(jar, 'r')
      contents = zip_file.infolist()
      for info in contents:
        file_name = info.filename

        if file_name.endswith('.class'):
          file_hash = ""
          with zip_file.open(file_name, "r") as afile:
              buf = afile.read(65536)
              crc_value = 1337
              while len(buf) > 0:
                  crc_value = zlib.crc32(buf, crc_value)
                  buf = afile.read(65536)
              file_hash = "%x"%(abs(crc_value))

          lst = res.get(file_name, [])
          lst.append([jar, file_hash])
          res[file_name] = lst
  final_res = {}
  for k,v in res.iteritems():
    if len(v) > 1 and len(set(map(lambda l: l[1], v))) > 1:
      final_res[k] = v
  return final_res

def print_duplicate_classes(jars):
  duplicates = build_duplicate_classes(jars)
  for k,v in duplicates.iteritems():
    print "Duplicate detected!: {key} is in {value}".format(key=k, value=v)

def main_jar(path, jars):
  candidates = filter(lambda j: os.path.basename(j).endswith(os.path.basename(path)), jars)
  if len(candidates) == 1:
    return candidates[0]
  else:
    raise Exception("many candidates: {candidates}".format(candidates=candidates))

hadoop_options = [
]

def build_launcher_script(jar, jars):
  return "\n".join(
    [ "#!/bin/bash"
    , "SCRIPTS_DIR=\"$( cd \"$( dirname \"${BASH_SOURCE[0]}\" )\" && pwd )\""
    , "cd $SCRIPTS_DIR"
    , "HADOOP_CLASSPATH={cp} /usr/local/hadoop/bin/hadoop jar {jar} {options} -libjars {libjars} --hdfs \"$@\"".format(
       cp = ":".join(jars),
       jar = jar,
       options = " ".join(hadoop_options),
       libjars = ",".join(jars))
    , ""
    ])

def get_main_class(binname):
  with open(binname, 'r') as b:
    lastline = b.read().split('\n')[-2]
    return lastline.split(' ')[-2]
