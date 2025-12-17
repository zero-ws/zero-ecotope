#!/usr/bin/env bash
mysql -u r2mo -P 3306 -h localhost < database/database-reinit.sql
mvn install -DskipTests=true -Dmaven.javadoc.skip=true
# mvn liquibase:update -e
mvn process-resources flyway:migrate -Dflyway.validateMigrationNaming=true
echo "数据库初始化完成！"
