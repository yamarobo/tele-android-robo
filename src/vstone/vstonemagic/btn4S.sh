#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT
echo "CMD_BTN4S" > menu.fifo
