#!/bin/bash

read -r -p "Are You Sure? [Y/n] " input
case $input in
    [yY][eE][sS]|[yY])
        echo "Yes"
        pssh -h ip_android -P "cd /data/tools/redis-3.2.8/src; ./redis-server redis3.conf "
        ;;

    [nN][oO]|[nN])
        echo "No"
            ;;

    *)
    echo "Invalid input..."
    exit 1
    ;;
esac
