#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT
#nohup aplay vol_up.wav &

set `amixer -D "hw:CODEC" get Speaker | grep 'Front Left: Playback'`
VOLUME=$4
echo $VOLUME
VOLUME=$(($VOLUME+5))
amixer -D "hw:CODEC" sset Speaker $VOLUME
alsactl store
