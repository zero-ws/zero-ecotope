package io.zerows.cosmic.plugins.client;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.Objects;

class RestClientAddOn extends AddOnBase<RestClient> {
    private static RestClientAddOn INSTANCE;

    private RestClientAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static RestClientAddOn of() {
        return INSTANCE;
    }

    @CanIgnoreReturnValue
    static RestClientAddOn of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new RestClientAddOn(vertx, config);
        }
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("all")
    public RestClientManager manager() {
        return RestClientManager.of();
    }

    @Override
    protected RestClient createInstanceBy(final String name) {
        final JsonObject options = this.config().options();
        RestClientConfig config = Ut.deserialize(options, RestClientConfig.class);
        if (Objects.isNull(config)) {
            config = new RestClientConfig();
        }
        return RestClient.createClient(this.vertx(), config);
    }
}
