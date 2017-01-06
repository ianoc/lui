import contextlib
import json
import os
import os.path
import sys
import sys
import time
import zipfile
import zlib
from shared_utilities import *

def get_args():
  with open(sys.argv[1]) as f:
      return json.load(f)

BLOCKSIZE = 65536
def hash_file(file_path):
  crc_value = 1337
  with open(file_path, 'rb') as afile:
      buf = afile.read(BLOCKSIZE)
      while len(buf) > 0:
          crc_value = zlib.crc32(buf, crc_value)
          buf = afile.read(BLOCKSIZE)
  return "%x"%(abs(crc_value))

def zipfile_name(file_path):
  hash_sig = hash_file(file_path)
  zip_file_name = "{hash_sig}-{basename}".format(hash_sig=hash_sig, basename=os.path.basename(file_path))
  zip_file_path = "{dirname}/{fname}".format(dirname = os.path.dirname(file_path), fname = zip_file_name)
  return (file_path, zip_file_path)

if __name__ == "__main__":
  args = get_args()
  with contextlib.closing(zipfile.ZipFile(args["output"], 'w')) as zipout:
      ignored = \
          [ "external/" + suffix for suffix in [ "local_jdk",
                                            "commons_",
                                            "org_apache_hadoop_hadoop_",
                                            "org_codehaus_jackson_",
                                            ]]
      def add_to_cp(jar):
        return jar and (len(filter(jar.startswith, ignored)) == 0)

      jars = filter(add_to_cp, args["classpath"])
      jars = map(zipfile_name, jars)
      [zipout.write(local_path, zip_path) for (local_path, zip_path) in jars]

      local_paths = map(lambda l: l[0], jars)
      zip_paths = map(lambda l: l[1], jars)
      script = build_launcher_script(zipfile_name(args["main_jar"])[1], zip_paths)

      print_duplicate_classes(local_paths)

      launch_name = "launch.sh"
      launch_info = zipfile.ZipInfo(filename=launch_name, date_time=time.localtime(time.time())[:6])
      launch_info.external_attr = 0775 << 16L
      zipout.writestr(launch_info, script)
