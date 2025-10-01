package io.zerows.epoch.corpus.database.jooq;

import io.r2mo.function.Fn;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.common.shared.app.KDatabase;
import io.zerows.epoch.corpus.database.cp.zdk.DataPool;
import io.zerows.epoch.corpus.database.exception._40065Exception500JooqConfiguration;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.plugins.Infix;
import org.jooq.Configuration;
import org.jooq.DSLContext;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Infusion
@SuppressWarnings("unchecked")
public class JooqInfix implements Infix {

    private static final OLog LOGGER = Ut.Log.database(JooqInfix.class);

    private static final ConcurrentMap<String, Configuration> CONFIGURATION
        = new ConcurrentHashMap<>();

    private static Vertx vertxRef;

    /*
     * 1. Step 1:
     *
     * Infusion architecture in zero framework
     * This method will be called when container booting
     * First Step to initialize the whole jooq configuration
     */
    public static void init(final Vertx vertx) {
        vertxRef = vertx;
        /*
         * Here initialize the static jooq configuration only
         * If there exist dynamic pool, it will process in `delay` loading processing
         */
        CONFIGURATION.putAll(Infix.init(YmlCore.inject.JOOQ, JooqPin::initConfiguration, JooqInfix.class));
    }

    private static Configuration configDelay(final DataPool pool) {
        final KDatabase database = pool.getDatabase();
        final String configurationKey = Objects.requireNonNull(database).getJdbcUrl();
        if (CONFIGURATION.containsKey(configurationKey)) {
            return configSafe(configurationKey);
        } else {
            final Configuration configuration = pool.configuration();
            CONFIGURATION.put(configurationKey, configuration);
            return configuration;
        }
    }

    private static Configuration configSafe(final String key) {
        Objects.requireNonNull(key);
        final Configuration configuration = CONFIGURATION.get(key);
        Fn.jvmKo(Objects.isNull(configuration), _40065Exception500JooqConfiguration.class);
        return configuration;
    }

    public static <T> JooqDsl getDao(final Class<T> clazz) {
        final Configuration configuration = configSafe(YmlCore.jooq.PROVIDER);
        return JooqDsl.init(vertxRef, configuration, clazz);
    }

    public static <T> JooqDsl getDao(final Class<T> clazz, final String key) {
        final Configuration configuration = configSafe(key);
        return JooqDsl.init(vertxRef, configuration, clazz);
    }

    public static <T> JooqDsl getDao(final Class<T> clazz, final DataPool pool) {
        final Configuration configuration = configDelay(pool);
        return JooqDsl.init(vertxRef, configuration, clazz);
    }

    /*
     * For secondary
     * DSLContext of three method:
     * 1. Static: vertx-jooq.yml -> provider
     * 2. Static/Configured: vertx-jooq.yml -> ( by Key )
     */
    public static DSLContext contextTrash() {
        return configSafe(YmlCore.jooq.ORBIT).dsl();
    }

    public static Configuration get(final String key) {
        return CONFIGURATION.get(key);
    }

    @Override
    public Configuration get() {
        return get(YmlCore.jooq.PROVIDER);
    }
}
