package io.zerows.epoch.configuration;

import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.YmlCore;
import io.zerows.sdk.environment.Transformer;

import java.util.Objects;

public class TransformerVertx implements Transformer<VertxOptions> {

    public static VertxOptions nativeOption() {
        final VertxOptions options = new VertxOptions();
        options.setMaxEventLoopExecuteTime(3000_000_000_000L);
        options.setMaxWorkerExecuteTime(3000_000_000_000L);
        options.setBlockedThreadCheckInterval(10000);
        options.setPreferNativeTransport(true);
        return options;
    }

    @Override
    public VertxOptions transform(final JsonObject input) {
        final JsonObject config = input.getJsonObject(YmlCore.vertx.OPTIONS, null);
        final VertxOptions options;
        if (Objects.isNull(config)) {
            options = new VertxOptions();
        } else {
            options = new VertxOptions(config);
        }
        options.setPreferNativeTransport(true);
        return options;
    }
}