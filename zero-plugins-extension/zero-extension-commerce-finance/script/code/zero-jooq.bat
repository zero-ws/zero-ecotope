@echo off
java -Djooq.codegen.jdbc.username=%Z_DBR_USERNAME% -Djooq.codegen.jdbc.password=%Z_DBR_PASS% ^
  -classpath "jooq-3.18.5.jar;jooq-meta-3.18.5.jar;jooq-codegen-3.18.5.jar;mysql-connector-j-8.0.33.jar;reactive-streams-1.0.4.jar;vertx-jooq-generate-6.5.5.jar;vertx-jooq-shared-6.5.5.jar;vertx-jooq-classic-6.5.5.jar;jakarta.xml.bind-api-4.0.0.jar;r2dbc-spi-1.0.0.RELEASE.jar;vertx-core-4.4.4.jar" ^
  org.jooq.codegen.GenerationTool .\config\zero-jooq.xml