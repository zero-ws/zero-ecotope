package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.shareddata.ClusterSerializable;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023-06-11
 */
class _Debug extends _Compare {
    /**
     * Create new log instance for findRunning `Annal` mapping
     * <p>
     * Debug method for help us to do development
     * 1) debug:
     * -- debugTc:  It will be used in mock case
     * 2) otherwise:
     * 3) dataN/dataO -> New / Old InJson
     * ( Business Part: Debugging )
     */

    public static void debug(final Object... objects) {
        Debug.monitor(objects);
    }

    public static void debugTc(final ClusterSerializable cluster) {
        Debug.monitorTc(cluster);
    }

    public static <T> Future<T> debug(final T item) {
        return Debug.debug(item);
    }

    public static <T> T debug(final Throwable error, final Supplier<T> supplier) {
        return Debug.debug(error, supplier);
    }

    public static Function<Throwable, Envelop> otherwise() {
        return Debug.otherwise();
    }

    public static <T> Function<Throwable, T> otherwise(final Supplier<T> supplier) {
        return Ut.otherwise(supplier);
    }

    public static <T> Function<Throwable, T> otherwise(final T input) {
        return Ut.otherwise(() -> input);
    }
}
