#!/usr/bin/python

import sys
import os
import time

path="/data/tools/gameiUtils/config/"


def file_replace(shardId,filename,zkip):
        file = open("config/"+str(shardId)+"/"+filename,'w')
        file_txt = read_file("config/config.js")
        file_txt = file_txt.replace("#shard_id#",str(shardId))
        file_txt = file_txt.replace("#zkip#", str(zkip))
        file_txt = file_txt.replace("#startdate#", str(time.strftime('%Y-%m-%d',time.localtime(time.time()))))
        print >> file,file_txt
        file.close()


def read_file(path):
        str=""
        try:
                fobj = open(path)
                for eachLine in fobj:
                        str=str+"\n"+eachLine.replace("\n","")
                fobj.close()
        except Exception,e:
                print "Error"
        return str

def shard_replace(shardId):
	os.system('rm -rf config/%d' % (shardId))
	os.system('mkdir config/%d' % (shardId))
	file_replace(shardId,"config.js", getzkip(shardId))

def getzkip(shardId):
    if(shardId > 2000 and shardId < 3000):
        return "172.18.145.103"
    elif(shardId > 3000 and shardId < 4000):
        return "172.18.145.95"
    elif (shardId > 4000 and shardId < 5000): 
        return "172.18.81.220"
    else: 
        return "127.0.0.1"

#def copy_all():
#	for n in range(101,240):
#		shard_copy(n)

def shard_copy(shardId):
	try:
		shard_replace(shardId)
        #return ""	
		ip = 'logic-%d' % (shardId)
		pathx = str(shardId)
		str_1=''
		str_1="scp -P22 %s root@%s:%s" % ("config/"+pathx+"/config.js",ip,'/data/ZGame/logic/config/')
		os.system(str_1)

	except Exception,e:
		print "Error", e
			

shardId = int(raw_input('> please shardId like this 101 --> '))

if shardId==0:
	copy_all()
	
if shardId>0:
	shard_copy(shardId)


