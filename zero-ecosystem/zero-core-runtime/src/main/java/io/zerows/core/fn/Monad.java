package io.zerows.core.fn;

import io.vertx.core.Future;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author lang : 2023/4/28
 */
class Monad {
    private static final OLog LOGGER = Ut.Log.ux(Monad.class);

    private Monad() {

    }

    /*
     * JvmSupplier -> Supplier
     */
    static <T> T monad(final Supplier<T> supplier, final T defaultValue) {
        try {
            return supplier.get();
        } catch (final Throwable ex) {
            // TODO: For Debug
            LOGGER.fatal(ex);
            ex.printStackTrace();
            return defaultValue;
        }
    }

    static <T> Future<T> monadAsync(final Supplier<Future<T>> supplier, final T defaultValue) {
        try {
            return supplier.get();
        } catch (final Throwable ex) {
            // TODO: For Debug
            LOGGER.fatal(ex);
            ex.printStackTrace();
            return Future.succeededFuture(defaultValue);
        }
    }

    static <T> void safeT(final Supplier<T> supplier, final Consumer<T> consumer) {
        final T input = supplier.get();
        if (Objects.nonNull(input)) {
            if (input instanceof String) {
                if (Ut.isNotNil((String) input)) {
                    consumer.accept(input);
                }
            } else {
                consumer.accept(input);
            }
        }
    }
}
