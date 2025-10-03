package io.zerows.epoch.database.cp;

import io.zerows.platform.enums.EmDS;
import org.jooq.SQLDialect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface DSMeta {

    @SuppressWarnings("all")
    ConcurrentMap<EmDS.Category, SQLDialect> DIALECT = new ConcurrentHashMap<>() {
        {
            // Jooq Supported Default
            // MySQL
            this.put(EmDS.Category.MYSQL8, SQLDialect.MYSQL);
            this.put(EmDS.Category.MYSQL5, SQLDialect.MYSQL);
            this.put(EmDS.Category.TIDB, SQLDialect.MYSQL);

            // PgSQL
            this.put(EmDS.Category.POSTGRES, SQLDialect.POSTGRES);
            this.put(EmDS.Category.COCKROACHDB, SQLDialect.POSTGRES);

            // Other
            this.put(EmDS.Category.MARIADB, SQLDialect.MARIADB);
            this.put(EmDS.Category.SQLLITE, SQLDialect.SQLITE);
            this.put(EmDS.Category.TRINO, SQLDialect.TRINO);
            this.put(EmDS.Category.YUGABYTEDB, SQLDialect.YUGABYTEDB);
            this.put(EmDS.Category.DERBY, SQLDialect.DERBY);
            this.put(EmDS.Category.FIREBIRD, SQLDialect.FIREBIRD);
            this.put(EmDS.Category.H2, SQLDialect.H2);
            this.put(EmDS.Category.HSQLDB, SQLDialect.HSQLDB);

            // Experimental / Deprecated
            this.put(EmDS.Category.DUCKDB, SQLDialect.DUCKDB);
            this.put(EmDS.Category.CUBRID, SQLDialect.CUBRID);
            this.put(EmDS.Category.IGNITE, SQLDialect.IGNITE);

            // Other will use DEFAULT instead for future
            for (EmDS.Category category : EmDS.Category.values()) {
                if (!this.containsKey(category)) {
                    this.put(category, SQLDialect.DEFAULT);
                }
            }
        }
    };
}
