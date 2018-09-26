#!/bin/bash
a=`cat /root/.ssh/id_rsa.pub`
read -t 30 -p "请输入主机:" hostip 
uname=`echo root`
pwd=`echo DRy!NlAl`
/usr/bin/expect <<-EOF
 set time 30
 spawn ssh $uname@$hostip
 expect {
 "*yes/no" { send "yes\r"; exp_continue }
"*password:" { send "$pwd\r" }
}
expect "*#"
send "echo $a >> /root/.ssh/authorized_keys\r"
expect "*#"
send "exit\r"
interact
expect eof
EOF
#echo "$pwd"
