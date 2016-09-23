#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT

. ./wlan.func

NOWCNT=`wpa_cli -iwlan0 status | grep id | tail -n 1 | cut -d "=" -f 2`
disable_all
setpriorityAll1
wpa_cli -iwlan0 disconnect
wpa_cli wps_pbc &

