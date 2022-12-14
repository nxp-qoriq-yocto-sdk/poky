DESCRIPTION = "Ncurses library"
HOMEPAGE = "http://www.gnu.org/software/ncurses/ncurses.html"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://ncurses/base/version.c;beginline=1;endline=27;md5=cbc180a8c44ca642e97c35452fab5f66"
SECTION = "libs"
PATCHDATE = "20100501"
PKGV = "${PV}+${PATCHDATE}"
PR = "r1"

DEPENDS = "ncurses-native"
DEPENDS_virtclass-native = ""

inherit autotools binconfig

SRC_URI = "${GNU_MIRROR}/ncurses/ncurses-${PV}.tar.gz;name=tarball \
        ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100424-patch.sh.bz2;apply=yes;name=p20100424sh \
\
        http://autobuilder.yoctoproject.org/sources/ncurses-5.7-${PATCHDATE}.patch.gz;name=p20100501 \
        file://tic-hang.patch \
        file://config.cache \
"


#        ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-${PATCHDATE}.patch.gz;name=p20100501 

SRC_URI[tarball.md5sum] = "cce05daf61a64501ef6cd8da1f727ec6"
SRC_URI[tarball.sha256sum] = "0a9bdea5c7de8ded5c9327ed642915f2cc380753f12d4ad120ef7da3ea3498f4"
SRC_URI[p20100424sh.md5sum] = "3a5f76613f0f7ec3e0e73b835bc24864"
SRC_URI[p20100424sh.sha256sum] = "1e9d70d2d1fe1fea471868832c52f1b9cc6065132102e49e2a3755f2f4f5be53"
SRC_URI[p20100501.md5sum] = "6518cfa5d45e9069a1e042468161448b"
SRC_URI[p20100501.sha256sum] = "a97ccc30e4bd6fbb89564f3058db0fe84bd35cfefee831556c500793b477abde"

#PARALLEL_MAKE = ""
EXTRA_AUTORECONF = "-I m4"
CONFIG_SITE =+ "${WORKDIR}/config.cache"

# Whether to enable separate widec libraries; must be 'true' or 'false'
#
# TODO: remove this variable when widec is supported in every setup?
ENABLE_WIDEC = "true"

# _GNU_SOURCE is required for widec stuff and is detected automatically
# for target objects.  But it must be set manually for native and sdk
# builds.
BUILD_CPPFLAGS += "-D_GNU_SOURCE"

# Override the function from the autotools class; ncurses requires a
# patched autoconf213 to generate the configure script. This autoconf
# is not available so that the shipped script will be used.
do_configure() {
        # check does not work with cross-compiling and is generally
        # broken because it requires stdin to be pollable (which is
        # not the case for /dev/null redirections)
        export cf_cv_working_poll=yes

        for i in \
        'narrowc' \
        'widec   --enable-widec --without-progs'; do
                set -- $i
                mkdir -p $1
                cd $1
                shift

                oe_runconf \
                        --disable-static \
                        --without-debug \
                        --without-ada \
                        --without-gpm \
                        --enable-hard-tabs \
                        --enable-xmc-glitch \
                        --enable-colorfgbg \
                        --with-termpath='${sysconfdir}/termcap:${datadir}/misc/termcap' \
                        --with-terminfo-dirs='${sysconfdir}/terminfo:${datadir}/terminfo' \
                        --with-shared \
                        --disable-big-core \
                        --program-prefix= \
                        --with-ticlib \
                        --with-termlib=tinfo \
                        --enable-sigwinch \
                        --enable-pc-files \
                        --disable-rpath-hack \
                        "$@"
                cd ..
        done
}

do_compile() {
        oe_runmake -C narrowc libs
        oe_runmake -C narrowc/progs

        ! ${ENABLE_WIDEC} || \
            oe_runmake -C widec libs
}

# set of expected differences between narrowc and widec header
#
# TODO: the NCURSES_CH_T difference can cause real problems :(
_unifdef_cleanup = " \
  -e '\!/\* \$Id: curses.wide,v!,\!/\* \$Id: curses.tail,v!d' \
  -e '/^#define NCURSES_CH_T /d' \
  -e '/^#include <wchar.h>/d' \
  -e '\!^/\* .* \*/!d' \
"

do_test[depends] = "unifdef-native:do_populate_sysroot"
do_test[dirs] = "${S}"
do_test() {
        ${ENABLE_WIDEC} || return 0

        # make sure that the narrow and widec header are compatible
        # and differ only in minor details.
        unifdef -k narrowc/include/curses.h | \
            sed ${_unifdef_cleanup} > curses-narrowc.h
        unifdef -k widec/include/curses.h | \
            sed ${_unifdef_cleanup} > curses-widec.h

        diff curses-narrowc.h curses-widec.h
}

_install_opts = "\
  DESTDIR='${D}' \
  PKG_CONFIG_LIBDIR='${libdir}/pkgconfig' \
  install.libs install.includes install.man \
"

do_install() {
        # Order of installation is important; widec installs a 'curses.h'
        # header with more definitions and must be installed last hence.
        # Compatibility of these headers will be checked in 'do_test()'.
        oe_runmake -C narrowc ${_install_opts} \
                install.data install.progs

        ! ${ENABLE_WIDEC} || \
            oe_runmake -C widec ${_install_opts}


        cd narrowc

        # include some basic terminfo files
        # stolen ;) from gentoo and modified a bit
        for x in ansi console dumb linux rxvt screen sun vt{52,100,102,200,220} xterm-color xterm-xfree86
        do
                local termfile="$(find "${D}${datadir}/terminfo/" -name "${x}" 2>/dev/null)"
                local basedir="$(basename $(dirname "${termfile}"))"

                if [ -n "${termfile}" ]
                then
                        install -d ${D}${sysconfdir}/terminfo/${basedir}
                        mv ${termfile} ${D}${sysconfdir}/terminfo/${basedir}/
                        ln -s /etc/terminfo/${basedir}/${x} \
                                ${D}${datadir}/terminfo/${basedir}/${x}
                fi
        done
        # i think we can use xterm-color as default xterm
        if [ -e ${D}${sysconfdir}/terminfo/x/xterm-color ]
        then
                ln -sf xterm-color ${D}${sysconfdir}/terminfo/x/xterm
        fi

        if [ "${PN}" = "ncurses" ]; then
                mv ${D}${bindir}/clear ${D}${bindir}/clear.${PN}
                mv ${D}${bindir}/reset ${D}${bindir}/reset.${PN}
        fi


        # create linker scripts for libcurses.so and libncurses to
        # link against -ltinfo when needed. Some builds might break
        # else when '-Wl,--no-copy-dt-needed-entries' has been set in
        # linker flags.
        for i in libncurses libncursesw; do
		f=${D}${libdir}/$i.so
                test -h $f || continue
                rm -f $f
                echo '/* GNU ld script */'  >$f
                echo "INPUT($i.so.5 AS_NEEDED(-ltinfo))" >>$f
        done

        # create libtermcap.so linker script for backward compatibility
        f=${D}${libdir}/libtermcap.so
        echo '/* GNU ld script */' >$f
        echo 'INPUT(AS_NEEDED(-ltinfo))' >>$f
}

python populate_packages_prepend () {
        libdir = bb.data.expand("${libdir}", d)
        pnbase = bb.data.expand("${PN}-lib%s", d)
        do_split_packages(d, libdir, '^lib(.*)\.so\..*', pnbase, 'ncurses %s library', prepend=True, extra_depends = '', allow_links=True)
}


pkg_postinst_ncurses-tools () {
        if [ "${PN}" = "ncurses" ]; then
                update-alternatives --install ${bindir}/clear clear clear.${PN} 100
                update-alternatives --install ${bindir}/reset reset reset.${PN} 100
        fi
}

pkg_prerm_ncurses-tools () {
        if [ "${PN}" = "ncurses" ]; then
                update-alternatives --remove clear clear.${PN}
                update-alternatives --remove reset reset.${PN}
        fi
}

BBCLASSEXTEND = "native nativesdk"

PACKAGES += " \
  ${PN}-tools \
  ${PN}-terminfo \
  ${PN}-terminfo-base \
"

FILES_${PN} = "\
  ${bindir}/tput \
  ${bindir}/tset \
  ${bindir}/ncurses5-config \
  ${bindir}/ncursesw5-config \
  ${datadir}/tabset \
"

# This keeps only tput/tset in ncurses
# clear/reset are in already busybox
FILES_${PN}-tools = "\
  ${bindir}/tic \
  ${bindir}/toe \
  ${bindir}/infotocap \
  ${bindir}/captoinfo \
  ${bindir}/infocmp \
  ${bindir}/clear.${PN} \
  ${bindir}/reset.${PN} \
  ${bindir}/tack \
  ${bindir}/tabs \
"
# 'reset' is a symlink to 'tset' which is in the 'ncurses' package
RDEPENDS_${PN}-tools = "${PN}"

FILES_${PN}-terminfo = "\
  ${datadir}/terminfo \
"

FILES_${PN}-terminfo-base = "\
  ${sysconfdir}/terminfo \
"

RSUGGESTS_${PN}-libtinfo = "${PN}-terminfo"
RRECOMMENDS_${PN}-libtinfo = "${PN}-terminfo-base"
