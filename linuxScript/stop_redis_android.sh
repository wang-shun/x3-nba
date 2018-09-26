#!/bin/bash

read -r -p "Are You Sure? [Y/n] " input
case $input in
    [yY][eE][sS]|[yY])
        echo "Yes"
        pssh -h ip_android -P "cd /data/tools/redis-3.2.8/src; ./redis-cli -a zgame2017 shutdown "
        ;;

    [nN][oO]|[nN])
        echo "No"
            ;;

    *)
    echo "Invalid input..."
    exit 1
    ;;
esac
