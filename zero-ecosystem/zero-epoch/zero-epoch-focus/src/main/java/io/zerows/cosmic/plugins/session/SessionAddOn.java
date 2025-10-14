package io.zerows.cosmic.plugins.session;

import com.google.inject.Key;
import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

/**
 * @author lang : 2025-10-14
 */
public class SessionAddOn implements AddOn<SessionClient> {
    private final Vertx vertx;
    private final HConfig config;
    private static final String NAME = "ADDON_SINGLETON_SESSION";
    private final SessionManager manager = SessionManager.of();

    static SessionAddOn INSTANCE;

    private SessionAddOn(final Vertx vertx, final HConfig config) {
        this.vertx = vertx;
        this.config = config;
    }

    @Override
    public Key<SessionClient> getKey() {
        return Key.get(SessionClient.class);
    }

    static SessionAddOn of(final Vertx vertx, final HConfig config) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new SessionAddOn(vertx, config);
        }
        return INSTANCE;
    }

    @Override
    public SessionClient createSingleton() {
        return this.manager.getClient(NAME, () -> this.createInstance(NAME));
    }

    @Override
    public SessionClient createInstance(final String name) {
        final SessionClient client = SessionClient.createClient(this.vertx, this.config);
        this.manager.putClient(name, client);
        return client;
    }
}
