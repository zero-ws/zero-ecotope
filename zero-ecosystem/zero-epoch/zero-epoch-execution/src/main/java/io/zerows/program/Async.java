package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.zerows.component.log.OLog;
import io.zerows.epoch.spi.channel.Pocket;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

class Async {

    private static final OLog LOGGER = Ut.Log.ux(Async.class);

    static <T> Future<T> fromAsync(final CompletionStage<T> state) {
        final Promise<T> promise = Promise.promise();
        state.whenComplete((result, error) -> {
            if (Objects.isNull(error)) {
                promise.complete(result);
            } else {
                promise.fail(error);
            }
        });
        return promise.future();
    }

    static <T> Future<T> future(final T input, final Set<Function<T, Future<T>>> set) {
        final List<Future<T>> futures = new ArrayList<>();
        set.stream().map(consumer -> consumer.apply(input)).forEach(futures::add);
        FnBase.combineT(futures).compose(nil -> {
            LOGGER.info("「Job Infusion」 There are `{0}` jobs that are finished successfully!", String.valueOf(set.size()));
            return ToCommon.future(nil);
        });
        return ToCommon.future(input);
    }

    @SuppressWarnings("all")
    static <T> Future<T> future(final T input, final List<Function<T, Future<T>>> queues) {
        if (0 == queues.size()) {
            /*
             * None queue here
             */
            return ToCommon.future(input);
        } else {
            Future<T> first = queues.get(VValue.IDX).apply(input);
            if (Objects.isNull(first)) {
                LOGGER.error("The index = 0 future<Tool> returned null, plugins will be terminal");
                return ToCommon.future(input);
            } else {
                if (1 == queues.size()) {
                    /*
                     * Get first future
                     */
                    return first;
                } else {
                    /*
                     * future[0]
                     *    .compose(future[1])
                     *    .compose(future[2])
                     *    .compose(...)
                     */
                    final KRef response = new KRef();
                    response.add(input);

                    for (int idx = 1; idx < queues.size(); idx++) {
                        final int current = idx;
                        first = first.compose(json -> {
                            final Future<T> future = queues.get(current).apply(json);
                            if (Objects.isNull(future)) {
                                /*
                                 * When null found, skip current
                                 */
                                return ToCommon.future(json);
                            } else {
                                return future
                                    /*
                                     * Replace the result with successed item here
                                     * If success
                                     * -- replace previous response with next
                                     * If handler
                                     * -- returned current json and replace previous response with current
                                     *
                                     * The step stopped
                                     */
                                    .compose(response::future)
                                    .otherwise(Ut.otherwise(() -> response.add(json).get()));
                            }
                        }).otherwise(Ut.otherwise(() -> response.get()));
                    }
                    return first;
                }
            }
        }
    }

    static <T> Function<Throwable, Future<T>> toErrorFuture(final Supplier<T> input) {
        return ex -> {
            if (Objects.nonNull(ex)) {
                ex.printStackTrace();
            }
            return Future.succeededFuture(input.get());
        };
    }


    static <T, O> Future<O> channel(final Class<T> clazz, final Supplier<O> supplier,
                                    final Function<T, Future<O>> executor) {
        final T channel = Pocket.lookup(clazz);
        if (Objects.isNull(channel)) {
            LOGGER.warn("「SL Channel」Channel {0} null", clazz.getName());
            return ToCommon.future(supplier.get());
        } else {
            LOGGER.debug("「SL Channel」Channel Async selected {0}, {1}",
                channel.getClass().getName(), String.valueOf(channel.hashCode()));
            return executor.apply(channel);
        }
    }


    static <T, O> O channelSync(final Class<T> clazz, final Supplier<O> supplier,
                                final Function<T, O> executor) {
        final T channel = Pocket.lookup(clazz);
        if (Objects.isNull(channel)) {
            LOGGER.warn("「SL Channel」Channel Sync {0} null", clazz.getName());
            return supplier.get();
        } else {
            LOGGER.debug("「SL Channel」Channel Sync selected {0}, {1}",
                channel.getClass().getName(), String.valueOf(channel.hashCode()));
            return executor.apply(channel);
        }
    }

    static <T, O> Future<O> channelAsync(final Class<T> clazz, final Supplier<Future<O>> supplier,
                                         final Function<T, Future<O>> executor) {
        final T channel = Pocket.lookup(clazz);
        if (Objects.isNull(channel)) {
            LOGGER.warn("「SL Channel」Channel Async {0} null", clazz.getName());
            return supplier.get();
        } else {
            LOGGER.debug("「SL Channel」Channel Async selected {0}, {1}",
                channel.getClass().getName(), String.valueOf(channel.hashCode()));
            return executor.apply(channel);
        }
    }
}
