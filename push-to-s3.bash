#!/bin/bash

if [[ `git rev-parse --abbrev-ref HEAD` = 'master' ]]; then
    aws s3 sync server/build/libs/ s3://fantasygc/deploy/
    aws s3 cp server/build/libs/fgc-*.jar s3://fantasygc/deploy/fgc-latest.jar
fi