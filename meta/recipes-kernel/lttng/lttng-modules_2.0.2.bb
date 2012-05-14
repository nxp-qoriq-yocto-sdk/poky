DESCRIPTION = "LTTng Kernel Tracing Modules"
LICENSE = "GPL"

LIC_FILES_CHKSUM = "file://LICENSE;md5=1eb086682a7c65a45acd9bcdf6877b3e"

DEPENDS += "virtual/kernel"

SRC_URI = "http://lttng.org/files/lttng-modules/lttng-modules-2.0.2.tar.bz2"
SRC_URI[md5sum] = "9d3ec7a1c9e3c5255c9c5fcd5c7f1ade"
SRC_URI[sha256sum] = "0fb4f537c3f1abc3a420142cb1c7ad9e93a3121b6995a0cc137862798a8bbe86"

inherit module

EXTRA_OEMAKE += "ARCH=${HOST_ARCH} INSTALL_MOD_PATH=${D}"
export KERNELDIR="${STAGING_KERNEL_DIR}"

do_install() {
    unset DEPMOD
    oe_runmake DEPMOD=/bin/true modules_install
}


