#!/bin/bash

#shard_id=
read -t 30 -p "请输入区ID:" shard_id 
#shard_ip=`ifconfig eth0 |grep "inet addr"| cut -f 2 -d ":"|cut -f 1 -d " "`
shard_ip=`ping logic-${shard_id} -c1 | grep PING | awk '{ print $3 }' |cut -d: -f1 |cut -d"(" -f2 |cut -d")" -f1`

sql="insert into game_servers(GameId, ServerID, mainName, serverName, sid, maxOnline, ip, withinIp, port, outPort, startTime, status, DBIP, DBPort, logic)
values (2002,#shard_id#,'测试服','测试服', #shard_id#, 5000, 'logic-#shard_id#.logic.ftxgame.com', '#shard_ip#', 9001, 8038, now(), 4, '#shard_ip#', 3306, 'logic-#shard_id#');"  

sql=${sql//\#shard_id\#/${shard_id}}
sql=${sql//\#shard_ip\#/${shard_ip}}

host="120.78.142.36"
user="root"  
passwd="dream_team_2011"  
dbname="web"  

#echo $shard_id
#echo $shard_ip
#echo $sql

mysql -h$host -u$user -p$passwd $dbname -e "$sql"
