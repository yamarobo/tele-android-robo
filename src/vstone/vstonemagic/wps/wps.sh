#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT

. ./wlan.func

if [ `wpa_cli status | grep wpa_state | grep 'SCANNING'` ] ; then
	echo 'CANCEL'
	wpa_cli wps_cancel
	aplay wps_cancel.wav  >/dev/null &

	wpa_cli save_config	
	enable_all
	systemctl restart wpa_supplicant

	killall wps.sh
else
  echo 'START'

NOWCNT=`wpa_cli -iwlan0 status | grep id | tail -n 1 | cut -d "=" -f 2`

echo "set priority"

disable_all
setpriorityAll1

echo "restart"
aplay wps.wav >/dev/null &
#systemctl restart wpa_supplicant

echo "disconnect"
wpa_cli -iwlan0 disconnect

echo "wps start"
wpa_cli wps_pbc &

while :
do
if [ `wpa_cli status | grep wpa_state | grep 'INACTIVE'` ] ; then
	echo 'timeout'
	aplay connect_error.wav  >/dev/null &

	enable_all
	setpriority5
	break
fi
if [ `wpa_cli status | grep wpa_state | grep 'COMPLETED'` ] ; then
	echo 'connected'
	aplay connected.wav  >/dev/null &

	enable_all
	setpriority5
	
	break
fi
sleep 2s
done

fi
