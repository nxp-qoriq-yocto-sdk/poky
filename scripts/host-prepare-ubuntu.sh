#!/bin/sh

if [ "`readlink /bin/sh`" = "dash" ]; then
    echo "Ubuntu uses dash as /bin/sh shell by default. It causes various subtle build problems in Yocto."
    echo "So, we need to reconfigure /bin/sh to point to bash in order to make Yocto build properly"
    echo "Please enter the password if needed and answer \"no\" to the following question:"

    sudo dpkg-reconfigure --terse -f readline dash
fi

PKGS="ubuntu-minimal ubuntu-standard patch vim-common xz-utils connect-proxy \
     sed wget cvs subversion git-core coreutils libbonobo2-common \
     unzip texi2html texinfo libsdl1.2-dev docbook-utils gawk \
     python-pysqlite2 diffstat help2man make gcc build-essential \
     g++ desktop-file-utils chrpath libgl1-mesa-dev libglu1-mesa-dev \
     mercurial autoconf automake groff libtool xterm libncurses5-dev"
if [ "`uname -m`" = "x86_64" ]; then
    PKGS="$PKGS ia32-libs lib32ncurses5"
fi

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo apt-get -y install $PKGS

