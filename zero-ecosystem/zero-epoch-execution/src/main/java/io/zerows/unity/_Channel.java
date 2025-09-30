package io.zerows.unity;

import io.vertx.core.Future;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023-06-11
 */
class _Channel {
    /*
     * Channel Execution
     *
     * 1. channel
     * 2. channelS
     * 3. channelA
     */
    public static <T, O> Future<O> channel(final Class<T> clazz, final Supplier<O> supplier,
                                           final Function<T, Future<O>> executor) {
        return Async.channel(clazz, supplier, executor);
    }


    public static <T, O> O channelS(final Class<T> clazz, final Supplier<O> supplier,
                                    final Function<T, O> executor) {
        return Async.channelSync(clazz, supplier, executor);
    }

    public static <T, O> O channelS(final Class<T> clazz, final Function<T, O> executor) {
        return Async.channelSync(clazz, () -> null, executor);
    }

    public static <T, O> Future<O> channelA(final Class<T> clazz, final Supplier<Future<O>> supplier,
                                            final Function<T, Future<O>> executor) {
        return Async.channelAsync(clazz, supplier, executor);
    }
}
