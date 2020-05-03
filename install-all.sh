#!/usr/bin/env bash

cd tesla-sample        && mvn clean install -U -DskipTests -Dmaven.javadoc.skip=true && cd ..
cd tesla-auth        && mvn clean install -U -DskipTests -Dmaven.javadoc.skip=true && cd ..
mvn clean package
