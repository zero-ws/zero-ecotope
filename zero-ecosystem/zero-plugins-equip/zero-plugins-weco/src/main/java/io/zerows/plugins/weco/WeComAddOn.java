package io.zerows.plugins.weco;

import io.vertx.core.Vertx;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

class WeComAddOn extends AddOnBase<WeComClient> {
    private static WeComAddOn INSTANCE;

    protected WeComAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static AddOn<WeComClient> of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new WeComAddOn(vertx, config);
        }
        return INSTANCE;
    }

    public static AddOn<WeComClient> of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected AddOnManager<WeComClient> manager() {
        return WeComManager.of();
    }

    @Override
    protected WeComClient createInstanceBy(final String name) {
        final WeCoConfig weCoConfig = WeCoAsyncManager.of().configOf(this.vertx(), this.config());
        return WeComClient.createClient(this.vertx(), weCoConfig);
    }
}
