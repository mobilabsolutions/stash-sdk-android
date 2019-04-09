#!/bin/bash

echo Git tag: ${TRAVIS_TAG}
echo Git bran: ${TRAVIS_BRANCH}
echo Travis build number: ${TRAVIS_BUILD_NUMBER}
echo Travis build id: ${TRAVIS_BUILD_ID}

# disable all the tests for now
#if [[ $TRAVIS_BRANCH == 'master' ]]; then
#./gradlew lib:uploadArchives lib:connectedCheck lib:test bspayone-integration:connectedCheck braintree-integration:connectedCheck
#else
#./gradlew lib:assemble
#fi
./gradlew lib:assemble sample:assembleDebug --stacktrace
