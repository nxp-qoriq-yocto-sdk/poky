require pango.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

PR = "r0"

SRC_URI += "file://no-tests.patch"

SRC_URI[archive.md5sum] = "caa74baea47e7e16bc73c89f9089d522"
SRC_URI[archive.sha256sum] = "5e278bc9430cc7bb00270f183360d262c5006b51248e8b537ea904573f200632"

#PARALLEL_MAKE = ""
