#!/bin/sh

UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y --force-yes';fi

# pkgs listed in yocto doc
# http://www.yoctoproject.org/docs/current/yocto-project-qs/yocto-project-qs.html#packages
PKGS="sed wget subversion git-core coreutils \
     unzip texi2html texinfo libsdl1.2-dev docbook-utils fop gawk \
     python-pysqlite2 diffstat make gcc build-essential xsltproc \
     g++ desktop-file-utils chrpath libgl1-mesa-dev libglu1-mesa-dev \
     autoconf automake groff libtool xterm libxml-parser-perl \
"
# pkgs required for fsl use
PKGS="$PKGS vim-common xz-utils cvs tofrodos libstring-crc32-perl"

# pkgs addedd in FSL SDK 1.2, need test to see if "actuall required"
if [ "Debian" != "$distro" ];then
    PKGS="$PKGS ubuntu-minimal ubuntu-standard"
fi
PKGS="$PKGS patch libbonobo2-common libncurses5-dev"
if [ "`uname -m`" = "x86_64" ]; then
    PKGS="$PKGS ia32-libs lib32ncurses5-dev"
fi

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo apt-get $UPDATE_FLAG install $PKGS

