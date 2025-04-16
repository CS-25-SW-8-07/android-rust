# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

project("rustnativecpp")

# Set Rust Target
if(${ANDROID_ABI} STREQUAL arm64-v8a)
    set(RUST_TARGET "aarch64-linux-android")
elseif (${ANDROID_ABI} STREQUAL armeabi-v7a)
    set(RUST_TARGET "armv7-linux-androidabi")
elseif (${ANDROID_ABI} STREQUAL x86)
    set(RUST_TARGET "i686-linux-android")
elseif (${ANDROID_ABI} STREQUAL x86_64)
    set(RUST_TARGET "x86_64-linux-android")
endif()

if(${CMAKE_HOST_SYSTEM_NAME} STREQUAL Linux)
    set(HOST_TAG "linux-x86_64")
elseif (${CMAKE_HOST_SYSTEM_NAME} STREQUAL Darwin)
    set(HOST_TAG "darwin-x86_64")
elseif (${CMAKE_HOST_SYSTEM_NAME} STREQUAL Windows)
    set(HOST_TAG "windows-x86_64")
endif()

if(${CMAKE_BUILD_TYPE} STREQUAL Release)
    set(RUST_RELEASE --release)
    set(RUST_OUT_DIR release)
else()
    set(RUST_OUT_DIR debug)
endif()

set(RUST_LINKER "${ANDROID_NDK}/toolchains/llvm/prebuilt/${HOST_TAG}/bin/${RUST_TARGET}21-clang++")

set(RUST_DIR ${CMAKE_SOURCE_DIR}/rusty-android)
set(RUST_TARGET_DIR ${RUST_DIR}/target)
set(RUST_LIB_NAME librusty_android.so)
set(RUST_LIB_PATH ${RUST_TARGET_DIR}/${RUST_TARGET}/${RUST_OUT_DIR}/${RUST_LIB_NAME})

# Create rust lib
add_custom_command(OUTPUT "${RUST_LIB_PATH}"
        COMMAND cargo build ${RUST_RELEASE} --target=${RUST_TARGET} --config target.${RUST_TARGET}.linker=\\\"${RUST_LINKER}\\\"
        WORKING_DIRECTORY "${RUST_DIR}"
        PRE_BUILD
)

set(RUST_MOVE_LIB ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/${RUST_LIB_NAME})
add_custom_command(
        OUTPUT ${RUST_MOVE_LIB}
        COMMAND cp ${RUST_LIB_PATH} ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}
        DEPENDS ${RUST_LIB_PATH}
)

add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        native-lib.cpp
        # NOTE: This is cheating but it works
        "${RUST_MOVE_LIB}"
)


message(OUTPUT=${CMAKE_RUNTIME_OUTPUT_DIRECTORY})

target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC "${RUST_LIB_PATH}")

message(${RUST_TARGET_DIR}/${RUST_TARGET}/debug/librusty_android.so)
target_link_libraries(${CMAKE_PROJECT_NAME} PUBLIC
        # List libraries link to the target library
        android
        log
        ${RUST_MOVE_LIB}
)

