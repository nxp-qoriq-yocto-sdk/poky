SUMMARY = "A general-purpose utility library"
DESCRIPTION = "GLib is a general-purpose utility library, which provides many useful data types, macros, type conversions, string utilities, file utilities, a main loop abstraction, and so on."
# pcre is under BSD;
# docs/reference/COPYING is with a 'public domai'-like license!
LICENSE = "LGPLv2+ & BSD & public domain"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://glib/glib.h;startline=4;endline=17;md5=a4332fe58b076f29d07c9c066d2967b6 \
                    file://gmodule/COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://gmodule/gmodule.h;startline=4;endline=17;md5=76ab161b37202cd004073c42fac276ed \
                    file://glib/pcre/COPYING;md5=266ebc3ff74ee9ce6fad65577667c0f4 \
                    file://glib/pcre/pcre.h;startline=11;endline=35;md5=cc0858840d3c312ec3e45b28077eb0d8 \
                    file://docs/reference/COPYING;md5=f51a5100c17af6bae00735cd791e1fcc"
BUGTRACKER = "http://bugzilla.gnome.org"
SECTION = "libs"

require glib-2.0.inc

DEPENDS = "glib-2.0-native gtk-doc-native virtual/libiconv virtual/libintl zip dbus"
DEPENDS_virtclass-native = "gtk-doc-native pkgconfig-native gettext-native dbus-native"
DEPENDS_virtclass-nativesdk = "libtool-nativesdk"
PACKAGES =+ "glib-2.0-utils "

LEAD_SONAME = "libglib-2.0.*"
FILES_glib-2.0-utils = "${bindir}/*"

inherit autotools pkgconfig gettext

S = "${WORKDIR}/glib-${PV}"

EXTRA_OECONF = "--disable-debug --enable-included-printf=no"
EXTRA_OECONF_virtclass-native = "--disable-dtrace --disable-systemtap"
EXTRA_OECONF_poky-lsb = "--enable-included-printf=no"

FILES_${PN} = "${libdir}/lib*${SOLIBS}"
FILES_${PN}-dev += "${libdir}/glib-2.0/include"

ARM_INSTRUCTION_SET = "arm"
