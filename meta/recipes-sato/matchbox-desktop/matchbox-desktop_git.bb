DESCRIPTION = "Matchbox Window Manager Desktop"
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.openedhand.com/"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://libtaku/eggsequence.h;endline=20;md5=b91f68f7642a1459fa1f4c9df94a8f15 \
                    file://src/desktop.c;endline=20;md5=36c9bf295e6007f3423095f405af5a2d \
                    file://src/main.c;endline=19;md5=2044244f97a195c25b7dc602ac7e9a00"

DEPENDS = "gtk+ startup-notification dbus"
SECTION = "x11/wm"
PV = "2.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://git.pokylinux.org/${BPN}-2;protocol=git \
           file://dso_linking_change_build_fix.patch \
           file://window-resize-fix.patch"

EXTRA_OECONF = "--enable-startup-notification --with-dbus"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
