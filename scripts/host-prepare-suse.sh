#!/bin/sh


PKGS="make patch python gcc gcc-c++ libtool xz \
     subversion git chrpath automake \
     help2man diffstat texinfo mercurial wget"

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo zypper install -y $PKGS

