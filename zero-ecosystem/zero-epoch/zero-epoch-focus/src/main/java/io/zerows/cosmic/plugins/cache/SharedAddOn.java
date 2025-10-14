package io.zerows.cosmic.plugins.cache;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.inject.Key;
import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-15
 */
@SuppressWarnings("all")
public class SharedAddOn implements AddOn<SharedClient> {
    private final Vertx vertx;
    private final HConfig config;
    private final SharedManager manager = SharedManager.of();

    static final String NAME = "ADDON_SINGLETON_SHARED";
    static SharedAddOn INSTANCE;

    private SharedAddOn(final Vertx vertx, final HConfig config) {
        this.vertx = vertx;
        this.config = config;
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
    public Key<SharedClient> getKey() {
        return Key.get(SharedClient.class);
    }

    @Override
    public SharedClient createSingleton() {
        return this.manager.getClient(NAME, () -> this.createInstance(NAME));
    }

    @Override
    public SharedClient createInstance(final String name) {
        final SharedClient client = SharedClient.createClient(this.vertx, name);
        this.manager.putClient(name, client);
        return client;
    }
}
