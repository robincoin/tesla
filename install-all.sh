#!/usr/bin/env bash


cd tesla-auth        && mvn install -U -DskipTests -Dmaven.javadoc.skip=true && cd ..
mvn clean package