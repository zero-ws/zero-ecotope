#!/usr/bin/env bash
mysql -u r2mo -P 3306 -h ox.engine.cn < database/database-reinit.sql
mvn install -DskipTests=true -Dmaven.javadoc.skip=true
mvn liquibase:update -e
echo "数据库初始化完成！"
