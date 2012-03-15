# Support for device tree generation
FILES_kernel-devicetree = "/boot/devicetree*"
KERNEL_DEVICETREE_FLAGS = "-R 8 -p 0x3000"

python __anonymous () {
    import bb

    devicetree = bb.data.getVar("KERNEL_DEVICETREE", d, 1) or ''
    if devicetree:
	depends = bb.data.getVar("DEPENDS", d, 1)
	bb.data.setVar("DEPENDS", "%s dtc-native" % depends, d)
	packages = bb.data.getVar("PACKAGES", d, 1)
	bb.data.setVar("PACKAGES", "%s kernel-devicetree" % packages, d)
}

do_install_append() {
    if test -n "${KERNEL_DEVICETREE}"; then
        for DTS_FILE in ${KERNEL_DEVICETREE}; do
            if [ ! -f ${DTS_FILE} ]; then
                echo "Warning: ${DTS_FILE} is not available!"
                continue
            fi
            DTS_BASE_NAME=`basename ${DTS_FILE} | awk -F "." '{print $1}'`
            DTB_NAME=`echo ${KERNEL_IMAGE_BASE_NAME} | sed "s/${MACHINE}/${DTS_BASE_NAME}/g"`
            DTB_SYMLINK_NAME=`echo ${KERNEL_IMAGE_SYMLINK_NAME} | sed "s/${MACHINE}/${DTS_BASE_NAME}/g"`
            dtc -I dts -O dtb ${KERNEL_DEVICETREE_FLAGS} -o devicetree ${DTS_FILE}
            install -m 0644 devicetree ${D}/boot/devicetree-${DTS_BASE_NAME}-${KERNEL_VERSION}
            install -d ${DEPLOY_DIR_IMAGE}
            install -m 0644 devicetree ${DEPLOY_DIR_IMAGE}/${DTB_NAME}.dtb
            cd ${DEPLOY_DIR_IMAGE}
            rm -f ${DTB_SYMLINK_NAME}.dtb
            ln -sf ${DTB_NAME}.dtb ${DTB_SYMLINK_NAME}.dtb
            cd -
        done
    fi
}

do_deploy_append() {
    if test -n "${KERNEL_DEVICETREE}"; then
        for DTS_FILE in ${KERNEL_DEVICETREE}; do
            if [ ! -f ${DTS_FILE} ]; then
                echo "Warning: ${DTS_FILE} is not available!"
                continue
            fi
            DTS_BASE_NAME=`basename ${DTS_FILE} | awk -F "." '{print $1}'`
            DTB_NAME=`echo ${KERNEL_IMAGE_BASE_NAME} | sed "s/${MACHINE}/${DTS_BASE_NAME}/g"`
            DTB_SYMLINK_NAME=`echo ${KERNEL_IMAGE_SYMLINK_NAME} | sed "s/${MACHINE}/${DTS_BASE_NAME}/g"`
            install -d ${DEPLOYDIR}
            install -m 0644 ${DEPLOY_DIR_IMAGE}/${DTB_NAME}.dtb ${DEPLOYDIR}/${DTB_NAME}.dtb
            cd ${DEPLOYDIR}
            ln -sf ${DTB_NAME}.dtb ${DTB_SYMLINK_NAME=}.dtb
        done
    fi
}
