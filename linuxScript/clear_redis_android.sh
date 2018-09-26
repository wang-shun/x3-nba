#!/bin/bash

read -r -p "Are You Sure? [Y/n] " input
case $input in
    [yY][eE][sS]|[yY])
        echo "Yes"
        pssh -h ip_android -P "cd /data/ZGame/logic/; echo -e "\n" | ./clearkey.py ; "
        ;;

    [nN][oO]|[nN])
        echo "No"
            ;;

    *)
    echo "Invalid input..."
    exit 1
    ;;
esac
