DESCRIPTION = "open source processor emulator"
HOMEPAGE = "http://qemu.org"
LICENSE = "GPLv2 & LGPLv2.1"
DEPENDS = "zlib"

EXTRA_OECONF = "--target-list=arm-linux-user,arm-softmmu,i386-linux-user,i386-softmmu,x86_64-linux-user,x86_64-softmmu,mips-linux-user,mips-softmmu,ppc-linux-user,ppc-softmmu,mipsel-linux-user --disable-werror --disable-vnc-tls --enable-kvm --audio-drv-list=oss,alsa --audio-card-list=ac97,es1370"
#EXTRA_OECONF += "--disable-sdl"

inherit autotools

# For our gl powered QEMU you need libGL and SDL headers
do_configure_prepend_virtclass-native() {
    libgl='no'
    libsdl='no'

    test -e /usr/lib/libGL.so -a -e /usr/lib/libGLU.so && libgl='yes'
    test -e /usr/lib64/libGL.so -a -e /usr/lib64/libGLU.so && libgl='yes'
    test -e /usr/lib/*-linux-gnu/libGL.so -a -e /usr/lib/*-linux-gnu/libGLU.so && libgl='yes'

    test -e /usr/lib/pkgconfig/sdl.pc -o -e /usr/lib64/pkgconfig/sdl.pc -o -e /usr/include/SDL/SDL.h && libsdl='yes'


    if [ "$libsdl" != 'yes' -o "$libgl" != 'yes' ]; then
       echo "You need libGL.so and libGLU.so to exist in your library path and the development headers for SDL installed to build qemu-native.
       Ubuntu package names are: libgl1-mesa-dev, libglu1-mesa-dev and libsdl1.2-dev"
       exit 1;
    fi
}

do_configure() {
    ${S}/configure --prefix=${prefix} ${EXTRA_OECONF}
    chmod a+x ${S}/target-i386/beginend_funcs.sh
}

SRC_URI_append_virtclass-nativesdk = " file://glflags.patch;patch=1"
DEPENDS_virtclass-nativesdk = "zlib-nativesdk libsdl-nativesdk qemugl-nativesdk"
RDEPENDS_virtclass-nativesdk = "libsdl-nativesdk"
EXTRA_OECONF_virtclass-nativesdk = "--target-list=arm-linux-user,arm-softmmu,i386-softmmu,x86_64-softmmu,mips-linux-user,mipsel-linux-user,mips-softmmu,ppc-softmmu --disable-vnc-tls --cross-prefix=${TARGET_PREFIX}"

BBCLASSEXTEND = "native nativesdk"
