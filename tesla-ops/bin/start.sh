#!/bin/sh
JAVA_OPTS="-Xmx640M -Xms640M -Xmn192M $JAVA_OPTS "
JAVA_OPTS="-XX:MaxMetaspaceSize=128M $JAVA_OPTS "
JAVA_OPTS="-XX:MetaspaceSize=128M $JAVA_OPTS "
JAVA_OPTS="-XX:+UseSerialGC $JAVA_OPTS "
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultConnectTimeout=10000"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultReadTimeout=30000"
java $JAVA_OPTS -jar ./app.jar