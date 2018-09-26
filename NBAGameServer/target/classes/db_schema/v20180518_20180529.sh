#!/usr/bin/env bash

#备份主线赛程 球队领取过赛区3星级礼包 的信息

sid=$1
db="nba_${sid}"
mysql -h logic-$1 -uroot -pzgame2017 -e"SELECT * FROM t_u_mmatch_div where star_awards >= 8;" ${db} > mmatch_star_gt2_${db}.txt
