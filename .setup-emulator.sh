#!/bin/bash
if [[ ${TRAVIS_BRANCH} == 'master' ]]; then
    echo no | android create avd --force -n test -t android-21 --abi armeabi-v7a
    emulator -avd test -no-audio -no-window &
    android-wait-for-emulator
    adb shell input keyevent 82 &
fi