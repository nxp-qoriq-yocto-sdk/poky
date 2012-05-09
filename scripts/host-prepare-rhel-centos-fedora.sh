#!/bin/sh

UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y';fi

echo "Install packages needed to build Yocto, please wait, it may take a while"
PKGS="tetex gawk sqlite-devel vim-common redhat-lsb xz \
     m4 make wget curl ftp tar bzip2 gzip python-devel \
     unzip perl texinfo texi2html diffstat openjade zlib-devel \
     docbook-style-dsssl sed docbook-style-xsl docbook-dtds \
     docbook-utils bc glibc-devel pcre pcre-devel \
     groff linuxdoc-tools patch cmake \
     tcl-devel gettext ncurses apr \
     SDL-devel mesa-libGL-devel mesa-libGLU-devel gnome-doc-utils \
     autoconf automake libtool xterm"
if [ "Fedora" = "$distro" ]; then
    PKGS="$PKGS ccache quilt perl-ExtUtils-MakeMaker connect-proxy"
fi
sudo yum $UPDATE_FLAG groupinstall "Development Tools"
sudo yum $UPDATE_FLAG install $PKGS

[ -r /etc/redhat-release ] && series=`sed -e 's,.*\([0-9]\)\..*,\1,g' /etc/redhat-release`
# chrpath is ONLY availabe on RHEL 6.x
if [ "Redhat" = "$distro" -a "6" != "$series" ]; then
    # check if chrpath is installed.
    if [ -z "$(rpm -qa chrpath)" ]; then
        echo "chrpath is required. Install it as follows:
        (1) download the package from http://mirror.centos.org/centos/${series}/extras/
        (2) sudo rpm -Uhv chrpath-0.13-3.el${series}.centos.i386.rpm
            or
            sudo rpm -Uhv chrpath-0.13-3.el${series}.centos.x86_64.rpm
        Then re-run this script."
    fi
else
    sudo yum $UPDATE_FLAG install chrpath
fi

