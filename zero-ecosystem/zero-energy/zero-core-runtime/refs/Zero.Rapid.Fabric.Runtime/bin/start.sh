#!/usr/bin/env bash
# Linux / MacOS
java -cp .:"${PWD}/conf" -Dfelix.cache.rootdir="${PWD}" -Dfelix.config.properties="file:///${PWD}/conf/config.properties" -jar libs/felix.jar
