#!/bin/sh

for i in $*; do
	if [[ $i = *.done ]]; then continue; fi
	/usr/bin/env wget --spider -q -t 5 --passive-ftp --no-check-certificate http://nas101.am.freescale.net/yocto/sstate-cache/`echo $i | sed -e 's/^.*\\///g'`
	rc=$?

	if [ $rc != 0 ]; then
		echo "$i was not used from cache!"
	fi
done
