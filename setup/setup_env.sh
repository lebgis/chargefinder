#! /bin/bash

export YOUR_HOME=<the location of your android install>

## android dev platform
#export ANDROID_API_PLATFORM=4
#export ANDROID_API_REVISION=r2
#export ANDROID_MAJORMINOR=1.6
#export ANDROID_REVISION=r3
#---
export ANDROID_API_PLATFORM=7
export ANDROID_API_REVISION=r1
export ANDROID_MAJORMINOR=2.1
export ANDROID_REVISION=r2
#---
#export ANDROID_API_PLATFORM=8
#export ANDROID_API_REVISION=r2
#export ANDROID_MAJORMINOR=2.2
#export ANDROID_REVISION=r2
#---
#export ANDROID_API_PLATFORM=9
#export ANDROID_API_REVISION=r1
#export ANDROID_MAJORMINOR=2.3
#export ANDROID_REVISION=r1
#---
export ANDROID_VERSION=${ANDROID_MAJORMINOR}_${ANDROID_REVISION}
export ANDROID_API_VERSION=${ANDROID_API_PLATFORM}_${ANDROID_API_REVISION}
export ANDROID_HOME=${YOUR_HOME}/android-sdk-mac_x86
export PATH=${ANDROID_HOME}/tools:${PATH}
export PATH=${ANDROID_HOME}/platform-tools:${PATH}