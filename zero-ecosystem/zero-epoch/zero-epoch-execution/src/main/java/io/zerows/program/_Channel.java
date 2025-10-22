package io.zerows.program;

import io.vertx.core.Future;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023-06-11
 */
@Slf4j
class _Channel {
    /*
     * Channel Execution
     *
     * 1. channel
     * 2. channelSync
     * 3. channelAsync
     */
    public static <T, O> Future<O> channel(final Class<T> clazz, final Supplier<O> supplier,
                                           final Function<T, Future<O>> executor) {
        return Optional.ofNullable(channelService(clazz))
            .map(executor)
            .orElseGet(() -> Future.succeededFuture(supplier.get()));
    }


    public static <T, O> O channelSync(final Class<T> clazz, final Supplier<O> supplier,
                                       final Function<T, O> executor) {
        return Optional.ofNullable(channelService(clazz))
            .map(executor)
            .orElseGet(supplier);
    }

    public static <T, O> O channelSync(final Class<T> clazz, final Function<T, O> executor) {
        return channelSync(clazz, () -> null, executor);
    }

    public static <T, O> Future<O> channelAsync(final Class<T> clazz, final Supplier<Future<O>> supplier,
                                                final Function<T, Future<O>> executor) {
        return Optional.ofNullable(channelService(clazz))
            .map(executor)
            .orElseGet(supplier);
    }

    private static <T, O> Future<O> invokeAsync(final Class<T> clazz,
                                                final Supplier<Future<O>> supplier,
                                                final Function<T, Future<O>> executor) {
        final T channel = channelService(clazz);
        if (Objects.isNull(channel)) {
            return supplier.get();
        } else {
            return executor.apply(channel);
        }
    }

    private static <T, O> O invokeSync(final Class<T> clazz,
                                       final Supplier<O> supplier,
                                       final Function<T, O> executor) {
        final T channel = channelService(clazz);
        if (Objects.isNull(channel)) {
            return supplier.get();
        } else {
            return executor.apply(channel);
        }
    }

    private static <T> T channelService(final Class<T> clazz) {
        final T channel = HPI.findOverwrite(clazz);
        if (Objects.isNull(channel)) {
            log.warn("[ ZERO ] 通道服务 Channel / 接口 = {} 未在环境中找到！", clazz.getName());
        }
        return channel;
    }
}
