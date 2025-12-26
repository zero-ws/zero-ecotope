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
        return Optional.ofNullable(findChannelComponent(clazz))
            .map(executor)
            .orElseGet(() -> Future.succeededFuture(supplier.get()));
    }

    private static <T> T findChannelComponent(final Class<T> clazz) {
        final T channel = HPI.findOverwrite(clazz);
        if (Objects.isNull(channel)) {
            log.warn("[ ZERO ] 通道服务 Channel / 接口 = {} 未在环境中找到！", clazz.getName());
        }
        return channel;
    }
}
