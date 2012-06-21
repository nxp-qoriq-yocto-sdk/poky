require dtc.inc

LIC_FILES_CHKSUM = "file://GPL;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://libfdt/libfdt.h;beginline=3;endline=52;md5=fb360963151f8ec2d6c06b055bcbb68c"

SRCREV = "be6026838e45b67800ac803f4ad8cca3cde57d6d"
PV = "1.3.0+git${SRCPV}"
PR = "${INC_PR}.1"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"
