#/bin/bash

DIRECTORY=`dirname $0`

SAGA="java -jar $DIRECTORY/utils/saga-2019-02-21.jar"
PATH_SEPARATOR=$($SAGA unix-win path-separator)

echo "---- setting classpath ------------------------------------------------"
CLASS_PATH=$(cd $DIRECTORY && mvn -q exec:exec -Dexec.executable=echo -Dexec.args="%classpath")
echo $CLASS_PATH | tr "$PATH_SEPARATOR" "\n"

echo "---- starting Saga Tools ----------------------------------------------"
java -cp $CLASS_PATH saga.SagaTools $*
