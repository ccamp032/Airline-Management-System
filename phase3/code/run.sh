#!/bin/bash

export hasRun
stop=$1

if [[ $stop = 1 ]]
then
    cd postgresql
    source ./stopPostgreDB.sh
    hasRun=0
    cd ..
else
    if [[ $hasRun != 1 ]]
    then
        cd postgresql
        source ./startPostgreSQL.sh
        sleep 2
        source ./createPostgreDB.sh
        sleep 1
        hasRun=1
        cd ..
    fi
    cd java
    ./compile.sh
    ./run.sh
    cd ..
fi