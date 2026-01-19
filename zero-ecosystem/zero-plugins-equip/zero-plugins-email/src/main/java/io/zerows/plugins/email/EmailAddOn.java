package io.zerows.plugins.email;

import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

class EmailAddOn extends AddOnBase<EmailClient> {
    private static EmailAddOn INSTANCE;

    protected EmailAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static AddOn<EmailClient> of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new EmailAddOn(vertx, config);
        }
        return INSTANCE;
    }

    static AddOn<EmailClient> of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected AddOnManager<EmailClient> manager() {
        return EmailManager.of();
    }

    @Override
    protected EmailClient createInstanceBy(final String name) {
        final EmailConfig emailServer = EmailManager.of().configOf(this.vertx(), this.config());
        return EmailClient.createClient(this.vertx(), emailServer);
    }
}
