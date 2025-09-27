#!/usr/bin/env bash
  mvn -f boot/Zero.Core.Entry.Facade clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f boot/Zero.Core.Entry.Import clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f boot/Zero.Core.Entry.Mini clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f boot/Zero.Core.Entry.Extension clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f boot/Zero.Core.Entry.OSGI clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true