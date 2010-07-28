#! /bin/bash

export ANDROID_PLATFORM=4
export ANDROID_VERSION=1.6_r3
export ANDROID_API_VERSION=${ANDROID_PLATFORM}_r2

#export ANDROID_PLATFORM=8
#export ANDROID_VERSION=2.2_r2
#export ANDROID_API_VERSION=${ANDROID_PLATFORM}_r2

export ANDROID_HOME=<your install location>/android-sdk-mac_x86
export PATH=${ANDROID_HOME}/tools:${PATH}
