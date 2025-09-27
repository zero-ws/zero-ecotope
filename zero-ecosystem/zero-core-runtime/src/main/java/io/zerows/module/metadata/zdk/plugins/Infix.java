package io.zerows.module.metadata.zdk.plugins;

import io.vertx.core.json.JsonObject;
import io.zerows.core.fn.RFn;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.exception.BootKeyMissingException;
import io.zerows.module.metadata.store.OZeroStore;
import io.zerows.module.metadata.uca.logging.OLog;

import java.util.function.Function;

public interface Infix {
    static <R> R init(final String key,
                      final Function<JsonObject, R> executor,
                      final Class<?> clazz) {
        final OLog logger = Ut.Log.plugin(clazz);
        RFn.outBoot(!OZeroStore.is(key), logger, BootKeyMissingException.class,
            clazz, key);
        final JsonObject options = OZeroStore.option(key);
        return executor.apply(options);
    }

    <T> T get();
}
