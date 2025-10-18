package io.zerows.epoch.database;

import io.vertx.core.json.JsonObject;
import io.zerows.component.environment.MatureOn;
import io.zerows.component.log.LogO;
import io.zerows.epoch.application.YmlCore;
import io.zerows.management.OZeroStore;
import io.zerows.platform.enums.EmDS;
import io.zerows.platform.metadata.KDatabase;
import io.zerows.support.Ut;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/*
 * Database linker for JDBC
 * {
 *      "hostname": "localhost",
 *      "instance": "DB_XXX",
 *      "username": "lang",
 *      "password": "xxxx",
 *      "port": 3306,
 *      "category": "MYSQL5",
 *      "driverClassName": "Fix driver issue here",
 *      "jdbcUrl": "jdbc:mysql://ox.engine.cn:3306/DB_XXX?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&useSSL=false&allowPublicKeyRetrieval=true",
 * }
 * I_SERVICE -> configDatabase
 *
 * YAML 格式说明
 * jooq:
 *    provider:         // PRIMARY
 *    orbit:            // HISTORY
 * workflow:
 *    database:         // WORKFLOW
 */
@Deprecated
public class OldDatabase extends KDatabase {
    private static final LogO LOGGER = Ut.Log.database(OldDatabase.class);
    private static OldDatabase OldDATABASE;

    /* Database Connection Testing */
    public static boolean test(final OldDatabase oldDatabase) {
        try {
            DriverManager.getConnection(oldDatabase.getUrl(), oldDatabase.getUsername(), oldDatabase.getPasswordDecrypted());
            return true;
        } catch (final SQLException ex) {
            // Debug for database connection
            ex.printStackTrace();
            OldDatabase.LOGGER.fatal(ex);
            return false;
        }
    }

    /**
     * <pre><code>
     * jooq:
     *     provider:
     * </code></pre>
     *
     * @return {@link OldDatabase}
     */
    public static OldDatabase getCurrent() {
        if (Objects.isNull(OldDATABASE)) {
            final JsonObject configJ = OZeroStore.option(YmlCore.jooq.__KEY); // Database.VISITOR.read();
            final JsonObject jooq = Ut.valueJObject(configJ, YmlCore.jooq.PROVIDER);
            OldDATABASE = configure(MatureOn.envDatabase(jooq, EmDS.DB.PRIMARY));
        }
        return OldDATABASE.copy();
    }


    /**
     * <pre><code>
     * jooq:
     *     orbit:
     * </code></pre>
     *
     * @return {@link OldDatabase}
     */
    public static OldDatabase getHistory() {
        final JsonObject configJ = OZeroStore.option(YmlCore.jooq.__KEY); // Database.VISITOR.read();
        final JsonObject jooq = Ut.valueJObject(configJ, YmlCore.jooq.ORBIT);
        return configure(MatureOn.envDatabase(jooq, EmDS.DB.HISTORY));
    }

    /**
     * <pre><code>
     * jooq:
     *     workflow:
     * </code></pre>
     *
     * @return {@link OldDatabase}
     */
    public static OldDatabase getCamunda() {
        final JsonObject configJ = OZeroStore.option(YmlCore.jooq.__KEY);
        final JsonObject jooq = Ut.valueJObject(configJ, YmlCore.jooq.WORKFLOW);
        return configure(MatureOn.envDatabase(jooq, EmDS.DB.WORKFLOW));
    }

    public static OldDatabase configure(final JsonObject databaseJ) {
        final JsonObject jooq = Ut.valueJObject(databaseJ);
        final OldDatabase oldDatabase = new OldDatabase();
        oldDatabase.fromJson(jooq);
        return oldDatabase;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OldDatabase copy() {
        final JsonObject json = this.toJson().copy();
        final OldDatabase oldDatabase = new OldDatabase();
        oldDatabase.fromJson(json);
        return oldDatabase;
    }
}
