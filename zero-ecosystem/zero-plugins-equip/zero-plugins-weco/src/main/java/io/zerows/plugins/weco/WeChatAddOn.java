package io.zerows.plugins.weco;

import io.vertx.core.Vertx;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

class WeChatAddOn extends AddOnBase<WeChatClient> {
    private static WeChatAddOn INSTANCE;

    protected WeChatAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static AddOn<WeChatClient> of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new WeChatAddOn(vertx, config);
        }
        return INSTANCE;
    }

    public static AddOn<WeChatClient> of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected AddOnManager<WeChatClient> manager() {
        return WeChatManager.of();
    }

    @Override
    protected WeChatClient createInstanceBy(final String name) {
        final WeCoConfig weCoConfig = WeCoAsyncManager.of().configOf(this.vertx(), this.config());
        return WeChatClient.createClient(this.vertx(), weCoConfig);
    }
}
