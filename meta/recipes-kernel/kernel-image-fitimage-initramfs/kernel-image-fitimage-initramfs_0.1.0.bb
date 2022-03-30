# Copyright (C) 2022 Claudius Heine <ch@denx.de>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Packages the fitimage with embedded initramfs"
LICENSE = "GPL-2.0-only"
DEPENDS = "virtual/kernel"

SRC_URI = ""

inherit kernel-artifact-names
INITRAMFS_IMAGE_NAME ?= "${@['${INITRAMFS_IMAGE}-${MACHINE}', ''][d.getVar('INITRAMFS_IMAGE') == '']}"
KERNEL_PACKAGE_NAME ?= "kernel"

PN = "${KERNEL_PACKAGE_NAME}-image-fitimage-initramfs"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install[deptask] = "do_deploy"
do_install() {
    fitimage="$(readlink "${DEPLOY_DIR_IMAGE}/fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_LINK_NAME}")"
    install -d ${D}/boot
    install -m 0644 \
        ${DEPLOY_DIR_IMAGE}/${fitimage} \
        ${D}/boot/
    ln -snf \
        ${fitimage} \
        ${D}/boot/fitImage
}

PACKAGES += "${KERNEL_PACKAGE_NAME}-image-initramfs"

FILES:${PN} = "/boot/fitImage /boot/fitImage-*"
RPROVIDES:${PN} = "${KERNEL_PACKAGE_NAME}-image-fitimage"
RCONFLICTS:${PN} = "${KERNEL_PACKAGE_NAME}-image-fitimage"
RREPLACES:${PN} = "${KERNEL_PACKAGE_NAME}-image-fitimage"

RDEPENDS:${KERNEL_PACKAGE_NAME}-image-initramfs = "${KERNEL_PACKAGE_NAME}-image-fitimage-initramfs (= ${EXTENDPGKV})"
RPROVIDES:${KERNEL_PACKAGE_NAME}-image-initramfs = "${KERNEL_PACKAGE_NAME}-image"
RCONFLICTS:${KERNEL_PACKAGE_NAME}-image-initramfs = "${KERNEL_PACKAGE_NAME}-image"
RREPLACES:${KERNEL_PACKAGE_NAME}-image-initramfs = "${KERNEL_PACKAGE_NAME}-image"
ALLOW_EMPTY:${KERNEL_PACKAGE_NAME}-image-initramfs = "1"

python() {
    if (not 'fitImage' in d.getVar('KERNEL_IMAGETYPES') or
            bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE'))):
        raise bb.parse.SkipRecipe('Requires generation of fitImage and bundled initramfs.')
    if not d.getVar('KERNEL_FIT_LINK_NAME'):
        raise bb.parse.SkipRecipe('Requires generation of fitImage symlink in deploy dir.')
}
