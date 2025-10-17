package io.zerows.cosmic.plugins.cache;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-15
 */
class SharedAddOn extends AddOnBase<SharedClient> {

    private static SharedAddOn INSTANCE;

    private SharedAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    public static SharedAddOn of() {
        return INSTANCE;
    }

    @CanIgnoreReturnValue
    static SharedAddOn of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new SharedAddOn(vertx, config);
        }
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("all")
    public SharedManager manager() {
        return SharedManager.of();
    }

    @Override
    protected SharedClient createInstanceBy(final String name) {
        return SharedClient.createClient(this.vertx(), name);
    }
}
