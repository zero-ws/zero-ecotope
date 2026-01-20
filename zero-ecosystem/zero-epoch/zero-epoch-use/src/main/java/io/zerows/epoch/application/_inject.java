package io.zerows.epoch.application;

/**
 * @author lang : 2023-05-29
 */
interface YmlInject {
    String __KEY = "inject";

    // 内部扩展
    String MONGO = "mongo";
    String MYSQL = "mysql";
    String REDIS = "redis";
    String RPC = "rpc";
    String JOOQ = "jooq";

    String SESSION = "session";

    String SHARED = "shared";

    String LOGGER = "logger";

    String SECURE = "secure";

    String TRASH = "trash";

    String ES = "elasticsearch";
    String NEO4J = "neo4j";
    String EXCEL = "excel";
}
