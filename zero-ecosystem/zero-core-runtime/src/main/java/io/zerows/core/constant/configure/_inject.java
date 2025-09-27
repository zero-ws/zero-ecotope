package io.zerows.core.constant.configure;

import io.zerows.core.annotations.infix.*;

/**
 * @author lang : 2023-05-29
 */
interface YmlInject {
    String __KEY = "inject";

    // 内部扩展
    /** {@link Mongo} */
    String MONGO = "mongo";
    /** {@link MySql} */
    String MYSQL = "mysql";
    /** {@link Redis} */
    String REDIS = "redis";
    /** {@link Rpc} */
    String RPC = "rpc";
    /** {@link Jooq} */
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
