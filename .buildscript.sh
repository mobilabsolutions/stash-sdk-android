#!/bin/bash

echo Git tag: ${TRAVIS_TAG}
echo Git bran: ${TRAVIS_BRANCH}
echo Travis build number: ${TRAVIS_BUILD_NUMBER}
echo Travis build id: ${TRAVIS_BUILD_ID}
echo Is this PR: ${TRAVIS_PULL_REQUEST}

if [ ! -z $TRAVIS_TAG ]; then
# If there's a tag, deploy.
    ./gradlew lib:assemble
elif [ $TRAVIS_PULL_REQUEST = 'false' ] && [ $TRAVIS_BRANCH = 'master' ]; then
# If it's not pull request and the branch is master, run all the tests.
    ./gradlew lib:connectedCheck lib:test bspayone-integration:connectedCheck braintree-integration:connectedCheck
else
# If it's pull request, run all the tests or just library tests?
	./gradlew clean lib:assemble lib:assembleAndroidTest --stacktrace
	./gradlew lib:check lib:connectedCheck --stacktrace
fi