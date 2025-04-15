# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("rustnativecpp")


message(ANDROID_ABI=${ANDROID_ABI})
if(${ANDROID_ABI} STREQUAL arm64-v8a)
    set(RUST_TARGET "aarch64-linux-android")
elseif (${ANDROID_ABI} STREQUAL armeabi-v7a)
    set(RUST_TARGET "armv7-linux-androidabi")
elseif (${ANDROID_ABI} STREQUAL x86)
    set(RUST_TARGET "i686-linux-android")
elseif (${ANDROID_ABI} STREQUAL x86_64)
    set(RUST_TARGET "x86_64-linux-android")
endif()
set(RUST_TARGET_DIR "${CMAKE_RUNTIME_OUTPUT_DIRECTORY}")

if(${CMAKE_HOST_SYSTEM_NAME} STREQUAL Linux)
    set(HOST_TAG "linux-x86_64")
elseif (${CMAKE_HOST_SYSTEM_NAME} STREQUAL Darwin)
    set(HOST_TAG "darwin-x86_64")
elseif (${CMAKE_HOST_SYSTEM_NAME} STREQUAL Windows)
    set(HOST_TAG "windows-x86_64")
endif()

if(${CMAKE_BUILD_TYPE} STREQUAL Release)
    set(RUST_RELEASE --release)
    endif()

message(RUST_TARGET="${RUST_TARGET} ${HOST_TAG}")
set(RUST_LINKER "${ANDROID_NDK}/toolchains/llvm/prebuilt/${HOST_TAG}/bin/${RUST_TARGET}21-clang++")
message(RUST_LINKER=${RUST_LINKER})

set(RUST_LIB_NAME librusty_android.so)
set(RUST_LIB ${RUST_TARGET_DIR}/${RUST_TARGET}/debug/${RUST_LIB_NAME})

# Create rust lib
add_custom_command(OUTPUT "${RUST_LIB}"
        COMMAND cargo build ${RUST_RELEASE} --target-dir ${RUST_TARGET_DIR} --target=${RUST_TARGET} --config target.${RUST_TARGET}.linker=\\\"${RUST_LINKER}\\\"
        WORKING_DIRECTORY "${CMAKE_SOURCE_DIR}/rusty-android"
        PRE_BUILD
)

set(RUST_MOVE_LIB ${RUST_LIB_NAME})
add_custom_command(
        OUTPUT ${RUST_MOVE_LIB}
        COMMAND cp ${RUST_LIB} ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}
        DEPENDS ${RUST_LIB}
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.

message(OUTPUT=${CMAKE_RUNTIME_OUTPUT_DIRECTORY})

add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        native-lib.cpp
        "${RUST_MOVE_LIB}"
)


message(OUTPUT=${CMAKE_RUNTIME_OUTPUT_DIRECTORY})

target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC "${RUST_LIB_PATH}")


# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
message(${RUST_TARGET_DIR}/${RUST_TARGET}/debug/librusty_android.so)
target_link_libraries(${CMAKE_PROJECT_NAME} PUBLIC
        # List libraries link to the target library
        android
        log
        "${RUST_LIB}"
)

