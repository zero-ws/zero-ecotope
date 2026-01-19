package io.zerows.plugins.email;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOn;

import java.util.Set;

@AddOn.Name("DEFAULT_EMAIL_CLIENT")
public interface EmailClient {

    static EmailClient createClient(final Vertx vertx, final EmailConfig emailServer) {
        return new EmailClientImpl(vertx, emailServer);
    }

    default Future<JsonObject> sendAsync(final String tplCode, final JsonObject params, final String to) {
        return this.sendAsync(tplCode, params, Set.of(to));
    }

    Future<JsonObject> sendAsync(String template, JsonObject params, Set<String> toSet);
}
