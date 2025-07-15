#!/bin/bash

DIRECTORY=`dirname $0`

SAGA="java -jar $DIRECTORY/utils/saga-2019-02-21.jar"
PATH_SEPARATOR=$($SAGA unix-win path-separator)

cd $DIRECTORY 
mvn -q exec:exec -Dexec.executable=echo -Dexec.args="%classpath"
