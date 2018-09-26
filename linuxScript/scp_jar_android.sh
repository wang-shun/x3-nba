#!/bin/bash

function CopyFile {
  echo 'copy ' $jarName ' to ' $1
  scp -r lib/$jarName root@$1:$targetPath
}

jarName='GameServer.zip'
targetPath='/data/ZGame/logic/'$jarName
echo 'file='$jarName
echo 'target='$targetPath
ls -l lib/$jarName

#复制
cat ip_android | while read line
do
hostip=`echo $line | cut -d" " -f1`
CopyFile $hostip
done


