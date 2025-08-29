#!/usr/bin/env bash
mvn clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true -Dmaven.compile.fork=true -T 1C
# 单独编译
# shellcheck disable=SC2164
cd zero-core-ams
echo "Zero AMS"
mvn clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true -Dmaven.compile.fork=true -T 1C
cd ..
# shellcheck disable=SC2164
cd zero-energy
echo "Zero Core"
mvn clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true -Dmaven.compile.fork=true -T 1C
cd ..
# shellcheck disable=SC2164
cd zero-plugins-equip
echo "Zero Plugins Equip"
mvn clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true -Dmaven.compile.fork=true -T 1C
cd ..
# shellcheck disable=SC2164
cd zero-plugins-extension
echo "Zero Plugins Extension"
mvn clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true -Dmaven.compile.fork=true -T 1C
cd ..
# shellcheck disable=SC2164
cd zero-plugins-external
echo "Zero Plugins External"
mvn clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true -Dmaven.compile.fork=true -T 1C
cd ..
echo "[END] Finished Compiled"