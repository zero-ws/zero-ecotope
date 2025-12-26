package io.zerows.plugins.integration.sms;

import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

class SmsAddon extends AddOnBase<SmsClient> {
    private static SmsAddon INSTANCE;

    protected SmsAddon(Vertx vertx, HConfig config) {
        super(vertx, config);
    }

    public static AddOn<SmsClient> of(Vertx vertx, HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new SmsAddon(vertx, config);
        }
        return INSTANCE;
    }

    public static AddOn<SmsClient> of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected AddOnManager<SmsClient> manager() {
        return SmsManager.of();
    }

    @Override
    protected SmsClient createInstanceBy(String name) {
        return SmsClient.createClient(this.vertx(),this.config());
    }
}