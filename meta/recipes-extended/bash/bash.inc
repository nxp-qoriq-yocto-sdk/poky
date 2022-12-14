DESCRIPTION = "An sh-compatible command language interpreter."
HOMEPAGE = "http://cnswww.cns.cwru.edu/~chet/bash/bashtop.html"
SECTION = "base/shell"

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "ncurses"

inherit autotools gettext

PARALLEL_MAKE = ""

bindir = "/bin"
sbindir = "/sbin"

EXTRA_OECONF = "--enable-job-control"
export CC_FOR_BUILD = "${BUILD_CC}"

ALTERNATIVE_NAME = "sh"
ALTERNATIVE_PATH = "${base_bindir}/bash"
ALTERNATIVE_LINK = "${base_bindir}/sh"
ALTERNATIVE_PRIORITY = "100"

do_configure () {
	gnu-configize
	oe_runconf
}

pkg_postinst_${PN} () {
	touch $D${sysconfdir}/shells
	grep -q "bin/bash" $D${sysconfdir}/shells || echo /bin/bash >> $D${sysconfdir}/shells
	grep -q "bin/sh" $D${sysconfdir}/shells || echo /bin/sh >> $D${sysconfdir}/shells
}
