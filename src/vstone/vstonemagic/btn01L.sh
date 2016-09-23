#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT
echo "CMD_BTN01L" > menu.fifo
