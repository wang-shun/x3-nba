#!/bin/bash

read -r -p "Are You Sure? [Y/n] " input
case $input in
    [yY][eE][sS]|[yY])
        echo "Yes"
        pssh -h ip_ios -P "cd /data/ZGame/logic; rm -rf lib/ bin/ excel/; ./unzipStart.py > /dev/null 2>&1 "
        ;;

    [nN][oO]|[nN])
        echo "No"
            ;;

    *)
    echo "Invalid input..."
    exit 1
    ;;
esac
