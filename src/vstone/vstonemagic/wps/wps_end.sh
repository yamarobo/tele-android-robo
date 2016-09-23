#!	/bin/sh
CURRENT=$(cd $(dirname $0) && pwd)
cd $CURRENT

. ./wlan.func

enable_all
setpriority5
