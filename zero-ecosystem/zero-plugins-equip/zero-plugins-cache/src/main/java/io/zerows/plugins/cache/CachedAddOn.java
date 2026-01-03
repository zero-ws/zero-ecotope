package io.zerows.plugins.cache;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2026-01-02
 */
class CachedAddOn extends AddOnBase<CachedClient> {
    private static CachedAddOn INSTANCE;

    private CachedAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static CachedAddOn of() {
        return INSTANCE;
    }

    @CanIgnoreReturnValue
    static CachedAddOn of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new CachedAddOn(vertx, config);
        }
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("all")
    public CachedManager manager() {
        return CachedManager.of();
    }

    @Override
    protected CachedClient createInstanceBy(final String name) {
        final JsonObject options = this.options();
        options.put(KName.NAME, name);
        return CachedClient.createClient(this.vertx(), options);
    }
}
