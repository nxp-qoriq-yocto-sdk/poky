#!/bin/sh

UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y';fi

PKGS="make patch python gcc gcc-c++ libtool xz \
     subversion git chrpath automake \
     diffstat texinfo wget"

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo zypper install $UPDATE_FLAG $PKGS

