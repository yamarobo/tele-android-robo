#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT

. ./wlan.func

#if [ $1 = $2 ] ; then
#    echo "error ssid = psk"
#    exit 0
#fi

systemctl enable wpa_supplicant
wpa_cli -iwlan0 disconnect
systemctl restart wpa_supplicant
sleep 2
wpa_cli -iwlan0 status >& /dev/null
if [ $? -ne 0 ]; then
echo -----------------------------retry 1
  sleep 2
  wpa_cli -iwlan0 status >& /dev/null
fi
if [ $? -ne 0 ]; then
echo -----------------------------retry 2
  sleep 2
  wpa_cli -iwlan0 status >& /dev/null
fi
if [ $? -ne 0 ]; then
echo -----------------------------retry 3
  sleep 2
  wpa_cli -iwlan0 status >& /dev/null
fi
if [ $? -ne 0 ]; then
echo -----------------------------retry 4
  sleep 2
  wpa_cli -iwlan0 status >& /dev/null
fi
if [ $? -ne 0 ]; then
echo -----------------------------retry 5
  sleep 2
  wpa_cli -iwlan0 status >& /dev/null
fi
if [ $? -ne 0 ]; then
echo -----------------------------retry 6
  sleep 2
  wpa_cli -iwlan0 status >& /dev/null
fi
if [ $? -ne 0 ]; then
echo -----------------------------timeout
  exit 1
fi


enable_all
setpriorityAll1

if wpa_cli -iwlan0 list_networks | grep $1 >/dev/null ; then
	echo found
	DELCONF=`wpa_cli -iwlan0 list_networks | grep $1 | tr '\t' ','| tr ' ' ',' | cut -d "," -f 1`
	wpa_cli -iwlan0 remove_network $DELCONF
fi

echo "add network"
NOWCONF=`wpa_cli -iwlan0 add_network`

echo "set ssid $1"
wpa_cli -iwlan0 set_network $NOWCONF ssid \"$1\"

if echo ${3} | grep WEP >/dev/null ; then
	echo "set key_mgmt NONE"
	wpa_cli -iwlan0 set_network $NOWCONF key_mgmt NONE
	echo "set group WEP104 WEP40"
	wpa_cli -iwlan0 set_network $NOWCONF group WEP104 WEP40
	echo "set wep_key0 $2"
	wpa_cli -iwlan0 set_network $NOWCONF wep_key0 \"$2\"
	#echo "set wep_tx_keyidx 0"
	#wpa_cli -iwlan0 set_network $NOWCONF wep_tx_keyidx 0
else
	echo "set psk $2"
	wpa_cli -iwlan0 set_network $NOWCONF psk \"$2\"
fi

wpa_cli -iwlan0 set_network $NOWCONF "priority" "5"

echo "enable network"
wpa_cli -iwlan0 enable_network $NOWCONF

echo "save config"
wpa_cli -iwlan0 save_config

sleep 3
systemctl restart wpa_supplicant

