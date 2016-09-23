#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT
while :
do
	VAR=`systemctl status 'pulseaudio' | grep 'active (running)'`

case $VAR in
*active*)
	echo 'active' >> pulseaudio.log
	break
;;
*)
	sleep 1
	echo 'inactive' >> pulseaudio.log
;;
esac
done
sleep 5

ldconfig

#systemctl status 'pulseaudio' &> pulseaudio.log
#aplay power/ohayou.wav &> alpay.log
aplay power/ohayou.wav
exit 0
