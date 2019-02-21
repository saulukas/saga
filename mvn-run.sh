#/bin/bash

echo "---- setting classpath ------------------------------------------------"
CLASSPATH=$(mvn -q exec:exec -Dexec.executable=echo -Dexec.args="%classpath")
echo $CLASSPATH | tr ":" "\n"

echo "---- starting Saga Tools ----------------------------------------------"
java -cp $CLASSPATH saga.SagaTools $*
