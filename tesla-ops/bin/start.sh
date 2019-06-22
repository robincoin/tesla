#!/bin/sh
JAVA_OPTS="-Xmx1344M -Xms1344M -Xmn448M -XX:MaxMetaspaceSize=256M $JAVA_OPTS "
JAVA_OPTS="-XX:MetaspaceSize=256M $JAVA_OPTS "
JAVA_OPTS="-XX:+UseConcMarkSweepGC $JAVA_OPTS "
JAVA_OPTS="-XX:+UseCMSInitiatingOccupancyOnly $JAVA_OPTS " 
JAVA_OPTS="-XX:CMSInitiatingOccupancyFraction=70 $JAVA_OPTS " 
JAVA_OPTS="-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses $JAVA_OPTS " 
JAVA_OPTS="-XX:+CMSClassUnloadingEnabled $JAVA_OPTS " 
JAVA_OPTS="-XX:+ParallelRefProcEnabled $JAVA_OPTS " 
JAVA_OPTS="-XX:+CMSScavengeBeforeRemark $JAVA_OPTS"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultConnectTimeout=10000"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultReadTimeout=30000"
JAVA_OPTS="${JAVA_OPTS} -Dserver.port=9000"
java $JAVA_OPTS -jar ./app.jar