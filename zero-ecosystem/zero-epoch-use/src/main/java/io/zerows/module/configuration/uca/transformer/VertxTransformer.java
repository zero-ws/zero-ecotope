package io.zerows.module.configuration.uca.transformer;

import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.module.configuration.zdk.Transformer;

import java.util.Objects;

public class VertxTransformer implements Transformer<VertxOptions> {

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