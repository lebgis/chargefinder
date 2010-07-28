#! /bin/bash

android create avd --target ${ANDROID_PLATFORM} --name AVD_${ANDROID_VERSION}-${ANDROID_API_VERSION} --sdcard 1000M