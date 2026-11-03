# SPDX-License-Identifier: Apache-2.0
#
# Copyright © 2017-2023 WireGuard LLC. All Rights Reserved.
#
# StarNest fork: only the userspace Go backend is built, and it is shipped as
# libawg-go.so so it can live in the same APK as com.wireguard.android:tunnel
# (which ships libwg-go.so / libwg.so / libwg-quick.so). The root-mode tools
# (awg / awg-quick, used only by AwgQuickBackend) are intentionally not built.

cmake_minimum_required(VERSION 3.4.1)
project("AmneziaWG")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}")
add_link_options(LINKER:--build-id=none)
add_link_options(-Wl,-z,max-page-size=16384)
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384")
add_compile_options(-Wall -Werror)

add_custom_target(libawg-go.so WORKING_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/libwg-go" COMMENT "Building amneziawg-go" VERBATIM COMMAND "${ANDROID_HOST_PREBUILTS}/bin/make"
    ANDROID_ARCH_NAME=${ANDROID_ARCH_NAME}
    ANDROID_PACKAGE_NAME=${ANDROID_PACKAGE_NAME}
    GRADLE_USER_HOME=${GRADLE_USER_HOME}
    CC=${CMAKE_C_COMPILER}
    CFLAGS=${CMAKE_C_FLAGS}
    LDFLAGS=${CMAKE_SHARED_LINKER_FLAGS}
    SYSROOT=${CMAKE_SYSROOT}
    TARGET=${CMAKE_C_COMPILER_TARGET}
    DESTDIR=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
    BUILDDIR=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/../generated-src
)
