package io.zerows.epoch.store;

import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-10-18
 */
@Slf4j
class DBSAddOn extends AddOnBase<DBClient> {

    private DBSAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    private static DBSAddOn INSTANCE;

    static DBSAddOn of(final Vertx vertx, final HConfig config) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DBSAddOn(vertx, config);
        }
        return INSTANCE;
    }

    static DBSAddOn of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected DBSManager manager() {
        return DBSManager.of();
    }

    @Override
    protected DBClient createInstanceBy(final String name) {
        return null;
    }
}
