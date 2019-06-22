#!/bin/sh
JAVA_OPTS="-Xmx2688M -Xms2688M -Xmn960M $JAVA_OPTS "
JAVA_OPTS="-XX:MaxMetaspaceSize=512M $JAVA_OPTS "
JAVA_OPTS="-XX:MetaspaceSize=512M $JAVA_OPTS "
JAVA_OPTS="-XX:+UseConcMarkSweepGC $JAVA_OPTS "
JAVA_OPTS="-XX:+UseCMSInitiatingOccupancyOnly $JAVA_OPTS "
JAVA_OPTS="-XX:CMSInitiatingOccupancyFraction=70 $JAVA_OPTS "
JAVA_OPTS="-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses $JAVA_OPTS "
JAVA_OPTS="-XX:+CMSClassUnloadingEnabled $JAVA_OPTS "
JAVA_OPTS="-XX:+ParallelRefProcEnabled $JAVA_OPTS "
JAVA_OPTS="-XX:+CMSScavengeBeforeRemark $JAVA_OPTS"
JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=9091,server=y,suspend=n $JAVA_OPTS"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultConnectTimeout=10000"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultReadTimeout=30000"
JAVA_OPTS="${JAVA_OPTS} -Dserver.port=9000"
java $JAVA_OPTS -jar ./app.jar



