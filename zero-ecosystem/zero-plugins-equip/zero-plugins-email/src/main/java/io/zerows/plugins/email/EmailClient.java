package io.zerows.plugins.email;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;

import java.util.Set;

@AddOn.Name("DEFAULT_EMAIL_CLIENT")
public interface EmailClient {

    static EmailClient createClient(final Vertx vertx, final HConfig config) {
        return new EmailClientImpl(vertx, config);
    }

    Future<JsonObject> sendAsync(String template, JsonObject params, Set<String> toSet);
}
