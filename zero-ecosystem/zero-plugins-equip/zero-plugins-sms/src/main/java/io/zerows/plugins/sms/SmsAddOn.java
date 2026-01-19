package io.zerows.plugins.sms;

import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

class SmsAddOn extends AddOnBase<SmsClient> {
    private static SmsAddOn INSTANCE;

    protected SmsAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static AddOn<SmsClient> of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new SmsAddOn(vertx, config);
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
    protected SmsClient createInstanceBy(final String name) {
        final SmsConfig smsConfig = SmsManager.of().configOf(this.vertx(), this.config());
        return SmsClient.createClient(this.vertx(), smsConfig);
    }
}