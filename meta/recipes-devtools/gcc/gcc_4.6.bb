require gcc-${PV}.inc
require gcc-configure-target.inc
require gcc-package-target.inc

RDEPENDS_${PN} += "libgcc binutils"

SRC_URI_append = "file://fortran-cross-compile-hack.patch"

ARCH_FLAGS_FOR_TARGET += "-isystem${STAGING_INCDIR}"
