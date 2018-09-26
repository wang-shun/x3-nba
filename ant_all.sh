#!/usr/bin/env bash
echo "publishing RouteServer"
ant -f RouteServer/build.xml build_run_daemon
echo "publishing RouteServer done. publishing PKNodeServer"
ant -f PKNodeServer/build.xml build_waiting_route_start
echo "publishing PKNodeServer done. publishing NBAGameServer"
ant -f NBAGameServer/build.xml build_waiting_pk_start
echo "publishing NBAGameServer done. publishing CrossNodeServer"
ant -f CrossNodeServer/build.xml build_waiting_logic_start
echo "publishing CrossNodeServer done."
