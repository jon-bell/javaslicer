#!/bin/bash

if [[ -n "$SUN_DIR" && -e $SUN_DIR ]]; then
  true # everything ok
elif [[ -e /usr/lib/jvm/java-6-sun ]]; then
  SUN_DIR=/usr/lib/jvm/java-6-sun
elif [[ -e /usr/lib/jvm/jdk1.6.0 ]]; then
  SUN_DIR=/usr/lib/jvm/jdk1.6.0
elif [[ -e /opt/sun-jdk-1.6.0.10 ]]; then
  SUN_DIR=/opt/sun-jdk-1.6.0.10
else
  echo "No java 6 directory found."
  exit 1
fi

MEMORY="5g"

runs=1
size=small

rm trace.log


# lusearch does not work

for testcase in antlr bloat chart eclipse fop hsqldb jython luindex lusearch pmd xalan; do
  echo doing test $testcase

  starttime=`date +'%s*1000+%N/1000000' | sed 's/+0*/+/'`
  
  time $SUN_DIR/bin/java -Xmx$MEMORY -jar dacapo-2006-10-MR2.jar -n $runs -s $size $testcase
  
  endtime=`date +'%s*1000+%N/1000000' | sed 's/+0*/+/'`
  millis=$(( ($endtime) - ($starttime) ))

  echo "##result: $testcase no_tracing: $millis msec"

  for compression in none gzip sequitur; do
  
    starttime=`date +'%s*1000+%N/1000000' | sed 's/+0*/+/'`

    time $SUN_DIR/bin/java -Xmx$MEMORY -javaagent:tracer.jar=logfile:trace.log,compression:$compression -jar dacapo-2006-10-MR2.jar -n $runs -s $size $testcase

    endtime=`date +'%s*1000+%N/1000000' | sed 's/+0*/+/'`
    millis=$(( ($endtime) - ($starttime) ))

    sleep 3
    fsize=`stat -c "scale=1; %b*%B/1024/1024" trace.log | bc`
    rm trace.log
  
    echo "##result: $testcase $compression: $millis msec, $fsize MB"
  done
done

