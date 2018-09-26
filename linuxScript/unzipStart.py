#!/usr/bin/python
# -*- coding: utf-8 -*-
import os
import shutil
import zipfile
if os.path.isdir("lib") :
  shutil.rmtree("lib")
if os.path.isdir("bin") :
  shutil.rmtree("bin")
if os.path.isdir("excel") :
  shutil.rmtree("excel")
#解压
azip = zipfile.ZipFile('GameServer.zip')
azip.extractall()
azip.close()
os.system("nohup ./app_8038.py &")
