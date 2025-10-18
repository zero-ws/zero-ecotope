package io.zerows.epoch.database.jooq;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.component.environment.MatureOn;
import io.zerows.component.log.LogO;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.OldDatabase;
import io.zerows.epoch.database.cp.DataPool;
import io.zerows.epoch.database.exception._40065Exception500JooqConfiguration;
import io.zerows.platform.enums.EmDS;
import io.zerows.support.Ut;
import org.jooq.Configuration;
import org.jooq.Table;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JooqPin {

    private static final LogO LOGGER = Ut.Log.database(JooqPin.class);

    public static String initTable(final Class<?> clazz) {
        final JooqDsl dsl = JooqInfix.getDao(clazz);
        final Table<?> table = Ut.field(dsl.dao(), KName.TABLE);
        return table.getName();
    }

    public static Class<?> initPojo(final Class<?> clazz) {
        final JooqDsl dsl = JooqInfix.getDao(clazz);
        return Ut.field(dsl.dao(), KName.TYPE);
    }

    static ConcurrentMap<String, Configuration> initConfiguration(final JsonObject config) {
        /*
         * config should be configured in vertx-jooq.yml
         * jooq:
         *    # Standard configuration
         *    provider:
         *    # History configuration
         *    orbit:
         */
        final ConcurrentMap<String, Configuration> configurationMap =
            new ConcurrentHashMap<>();

        Fn.jvmKo(Ut.isNil(config) || !config.containsKey(YmlCore.jooq.PROVIDER),
            _40065Exception500JooqConfiguration.class);

        if (Ut.isNotNil(config)) {
            /*
             * provider / orbit
             * provider - 标准数据库
             * orbit - 历史数据库
             */
            config.fieldNames().stream()
                // 过滤：key值对应的配置存在，并且是合法的 Database InJson 格式
                .filter(key -> Objects.nonNull(config.getValue(key)))
                .filter(key -> config.getValue(key) instanceof JsonObject)
                .forEach(key -> {
                    final JsonObject options = config.getJsonObject(key);
                    // Database Environment Connected
                    final JsonObject databaseJ;
                    if (YmlCore.jooq.ORBIT.equals(key)) {
                        databaseJ = MatureOn.envDatabase(options, EmDS.DB.HISTORY);
                    } else {
                        databaseJ = MatureOn.envDatabase(options, EmDS.DB.PRIMARY);
                    }
                    final DataPool pool = DataPool.create(OldDatabase.configure(databaseJ));
                    final Configuration configuration = pool.configuration();
                    configurationMap.put(key, configuration);
                    final JsonObject populated = databaseJ.copy();
                    populated.remove(KName.PASSWORD);
                    LOGGER.info("Jooq options: \n{0}", populated.encodePrettily());
                });
        }
        return configurationMap;
    }
}
