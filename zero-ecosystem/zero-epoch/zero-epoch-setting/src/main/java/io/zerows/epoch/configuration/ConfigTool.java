package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmVertx;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author lang : 2025-10-09
 */
final class ConfigTool {
    static boolean isVertx(final EmApp.Native name) {
        return Objects.isNull(name) || (EmApp.Native.DEPLOYMENT != name
            && EmApp.Native.DELIVERY != name
            && EmApp.Native.SHARED != name);
    }

    static void putOptions(final BiConsumer<EmApp.Native, HConfig> consumer,
                           final Object... data) {
        Arrays.stream(data).forEach(each -> {
            if (each instanceof final YmVertx.Delivery delivery) {
                final ConfigNorm configNorm = new ConfigNorm();
                consumer.accept(EmApp.Native.DELIVERY, configNorm.putOptions(Ut.serializeJson(delivery)));
            }
            if (each instanceof final YmVertx.Deployment deployment) {
                final ConfigNorm configNorm = new ConfigNorm();
                consumer.accept(EmApp.Native.DEPLOYMENT, configNorm.putOptions(Ut.serializeJson(deployment)));
            }
            if (each instanceof final JsonObject shared) {
                final ConfigNorm configNorm = new ConfigNorm();
                consumer.accept(EmApp.Native.SHARED, configNorm.putOptions(shared));
            }
        });
    }
}
