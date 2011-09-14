#!/bin/sh

# This script can be used to prepare host packages for Yocto build.
# 
# sudo permission without password is required for the following command:
# (1) Redhat/Fedora/CentOS: yum
# (2) Ubuntu: apt-get
# (3) openSUSE/SUSE: zypper
#
# The following platforms were tested:
# Ubuntu: 10.04
# Fedora: 15
# RHEL/CeniOS: 5
# openSUSE: 11.4

SCRIPT_DIR=`readlink -f $(dirname $0)`

# check host distribution
if [ -r /etc/lsb-release ] && grep Ubuntu /etc/lsb-release >/dev/null 2>&1
then
    # Ubuntu-based system
    . /etc/lsb-release
    distro="Ubuntu"
    release=${DISTRIB_RELEASE}
    hostpkg="apt-get"
elif [ -r /etc/fedora-release ]
then
    # Fedora-based
    distro="Fedora"
    release=`sed -e 's/.*release \([0-9]*\).*/\1/' /etc/fedora-release`
    hostpkg="yum"
elif [ -r /etc/SuSE-release ]
then
    # SUSE-based.
    if grep openSUSE /etc/SuSE-release >/dev/null 2>&1
    then
        distro="openSUSE"
    else
        distro="SUSE"
    fi
    release=`cat /etc/SuSE-release | grep "VERSION" | sed -e 's/VERSION = //'`
    hostpkg="zypper"
elif [ -r /etc/redhat-release ]; then
    # Redhat-based
    if grep CentOS /etc/redhat-release >/dev/null 2>&1
    then
        distro="CentOS"
    else
        distro="Redhat"
    fi
    release=`cat /etc/redhat-release | sed -e 's/[A-Za-z ]* release //'`
    hostpkg="yum"
else
    echo "Error: Unsupported Distribution, Exit"
    exit 1
fi
export distro

echo "Verifying sudo permission to execute $hostpkg command."
user=`whoami` || true
hostpkg_path=`which $hostpkg` || true
if [ -z "$hostpkg_path" ]; then
    echo "$hostpkg is required. Please install it and re-run this script."
    exit 1
fi
s=`yes "" | sudo -S -l 2>&1` || true
hostpkg_all=`echo $s | egrep "NOPASSWD:.*ALL"` || true
hostpkg_ok=`echo $s | egrep "NOPASSWD:.*$hostpkg_path"` || true
if [ -z "$hostpkg_ok" -a -z "$hostpkg_all" ]; then cat <<TXT
I ran the command: sudo -S -l which returned:

$s

This means you don't have sudo permission without password to execute 
$hostpkg command as root. This is needed to install host packages correctly.

To configure this, as root using the command "/usr/sbin/visudo",
and add the following line in the User privilege section:

$user ALL = NOPASSWD: $hostpkg_path

TXT
exit 1
fi

case "$distro" in
    'Ubuntu')
        script="$SCRIPT_DIR/host-prepare-ubuntu.sh";
        ;;
    'Redhat' | 'CentOS' | 'Fedora')
        script="$SCRIPT_DIR/host-prepare-rhel-centos-fedora.sh";
        ;;
    'SUSE' | 'openSUSE')
        script="$SCRIPT_DIR/host-prepare-suse.sh";
        ;;
esac

/bin/sh $script

# Make sure python is 2.x (2.6 or greater)
PYTHON_PATH="/opt/python2.6"
PYTHON_v26=`/usr/bin/env python -c 'import sys
print (sys.version_info >= (2, 6) and "1" or "0")'`
PYTHON_v3=`/usr/bin/env python -c 'import sys
print (sys.version_info >= (3, 0) and "1" or "0")'`
if [ "$PYTHON_v26" = '0' -o "$PYTHON_v3" = '1' ]; then
    echo "Python version 2.x (2.6 or greater) is required. For example, install 2.6.6 at $PYTHON_PATH:
    (1) wget http://www.python.org/ftp/python/2.6.6/Python-2.6.6.tgz
    (2) tar -zxvf Python-2.6.6.tgz
    (3) cd Python-2.6.6
    (4) ./configure --prefix=$PYTHON_PATH && make
    (5) su -
    (6) make install
Then execute the following command and re-run this script:
    export PATH=$PYTHON_PATH/bin:\$PATH"
    exit 1
fi

# check if connect-proxy is available.
if [ "Fedora" != "$distro" -a "Ubuntu" != "$distro" ]; then
    if [ ! -x /usr/bin/connect-proxy ]; then
        echo "connect-proxy is required. Install it as follows:
        (1) compile the $SCRIPT_DIR/oe-git-proxy-socks.c
            $ gcc $SCRIPT_DIR/oe-git-proxy-socks.c -o connect-proxy
        (2) move the binary to directory /usr/bin as root:
            $ mv connect-proxy /usr/bin
        Then re-run this script."
    fi
fi

echo "Done. You're ready to go with Yocto build now"

