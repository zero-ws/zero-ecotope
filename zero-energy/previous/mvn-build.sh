#!/usr/bin/env bash
# Runtime
mvn -f source/Zero.Core.Runtime.Metadata clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
# Depend On Runtime.Metadata
  mvn -f source/Zero.Core.Runtime.Assembly clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f source/Zero.Core.Runtime.Configuration clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f source/Zero.Core.Runtime.Domain clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
    mvn -f source/Zero.Core.Runtime.Cloud clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
    mvn -f source/Zero.Core.Runtime.Security clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
      mvn -f source/Zero.Core.Feature.Web.Client clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true

  mvn -f source/Zero.Core.Feature.Database.CP clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
    mvn -f source/Zero.Core.Feature.Database.JOOQ clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
      mvn -f source/Zero.Core.Feature.Database.Cache clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true

  mvn -f source/Zero.Core.Feature.Toolkit.Expression clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true

  mvn -f source/Zero.Core.Feature.Web.Session clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f source/Zero.Core.Feature.Web.Cache clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true

# Feature.Database.Cache
# Runtime.Assembly
mvn -f source/Zero.Core.Feature.Unit.Testing clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true

# Feature.Database.Cache
# Runtime.Security
# Runtime.Cloud
# Runtime.Assembly
# Runtime.Configuration
mvn -f source/Zero.Core.Web.Model clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f source/Zero.Core.Web.Invocation clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
    mvn -f source/Zero.Core.Web.IO clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
      mvn -f source/Zero.Core.Feature.Web.Security clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
      mvn -f source/Zero.Core.Feature.Web.WebSocket clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
      mvn -f source/Zero.Core.Web.Scheduler clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
      mvn -f source/Zero.Core.Web.Validation clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true

# Feature.Web.Cache
# Web.Scheduler
mvn -f source/Zero.Core.Feature.Web.Utility.X clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
  mvn -f source/Zero.Core.Feature.Web.MBSE clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
    mvn -f source/Zero.Core.Feature.Web.Monitor clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true

mvn -f source/Zero.Core.Web.Container clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true
# Compare this snippet from zero-ws/zero-energy/mvn-release.sh: