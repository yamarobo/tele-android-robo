#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT
aplay ohayou.wav >/dev/null &
