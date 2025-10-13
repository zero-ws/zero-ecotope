package io.zerows.epoch.database.cp;

import io.zerows.platform.enums.EmDS;
import org.jooq.SQLDialect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface DSMeta {

    @SuppressWarnings("all")
    ConcurrentMap<EmDS.Database, SQLDialect> DIALECT = new ConcurrentHashMap<>() {
        {
            // Jooq Supported Default
            // MySQL
            this.put(EmDS.Database.MYSQL8, SQLDialect.MYSQL);
            this.put(EmDS.Database.MYSQL5, SQLDialect.MYSQL);
            this.put(EmDS.Database.TIDB, SQLDialect.MYSQL);

            // PgSQL
            this.put(EmDS.Database.POSTGRES, SQLDialect.POSTGRES);
            this.put(EmDS.Database.COCKROACHDB, SQLDialect.POSTGRES);

            // Other
            this.put(EmDS.Database.MARIADB, SQLDialect.MARIADB);
            this.put(EmDS.Database.SQLLITE, SQLDialect.SQLITE);
            this.put(EmDS.Database.TRINO, SQLDialect.TRINO);
            this.put(EmDS.Database.YUGABYTEDB, SQLDialect.YUGABYTEDB);
            this.put(EmDS.Database.DERBY, SQLDialect.DERBY);
            this.put(EmDS.Database.FIREBIRD, SQLDialect.FIREBIRD);
            this.put(EmDS.Database.H2, SQLDialect.H2);
            this.put(EmDS.Database.HSQLDB, SQLDialect.HSQLDB);

            // Experimental / Deprecated
            this.put(EmDS.Database.DUCKDB, SQLDialect.DUCKDB);
            this.put(EmDS.Database.CUBRID, SQLDialect.CUBRID);
            this.put(EmDS.Database.IGNITE, SQLDialect.IGNITE);

            // Other will use DEFAULT instead for future
            for (EmDS.Database category : EmDS.Database.values()) {
                if (!this.containsKey(category)) {
                    this.put(category, SQLDialect.DEFAULT);
                }
            }
        }
    };
}
