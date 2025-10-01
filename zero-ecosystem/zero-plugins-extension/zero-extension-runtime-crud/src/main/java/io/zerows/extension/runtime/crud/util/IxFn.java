package io.zerows.extension.runtime.crud.util;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VValue;
import io.zerows.core.uca.aop.Aspect;
import io.zerows.core.util.Ut;
import io.zerows.core.web.mbse.atom.specification.KModule;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.unity.Ux;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class IxFn {

    // JqFn
    @SafeVarargs
    static <T> Future<T> pass(final T input, final IxMod in,
                              final BiFunction<T, IxMod, Future<T>>... executors) {
        // Sequence for future management
        Future<T> future = Future.succeededFuture(input);
        for (final BiFunction<T, IxMod, Future<T>> executor : executors) {
            if (Objects.nonNull(executor)) {
                future = future.compose(data -> executor.apply(data, in));
            }
        }
        return future;
    }

    @SafeVarargs
    static <T> Future<T> peek(
        final T data, final IxMod in,
        final Supplier<T> defaultSupplier,
        final BiFunction<T, IxMod, Future<T>>... executors) {
        if (0 == executors.length) {
            return Ux.future(defaultSupplier.get());
        }
        Future<T> first = executors[VValue.IDX].apply(data, in);
        for (int start = 1; start < executors.length; start++) {
            final int current = start;
            first = first.compose(queried -> {
                final boolean ifContinue;
                if (queried instanceof final JsonObject json) {
                    ifContinue = Ut.isNil(json);
                } else {
                    ifContinue = Objects.isNull(queried);
                }
                if (ifContinue) {
                    return executors[current].apply(data, in);
                } else {
                    return Ux.future(queried);
                }
            });
        }
        return first;
    }

    @SafeVarargs
    static <T> Future<T> park(final T input, final IxMod in,
                              final BiFunction<T, IxMod, Future<T>>... executors) {
        // 先检查异常
        final WebException error = in.error();
        if (Objects.nonNull(error)) {
            return Future.failedFuture(error);
        }
        return pass(input, in, executors);
    }


    static <T> Function<T, Future<T>> aop(
        final KModule module, final BiFunction<Aspect, Function<T, Future<T>>, Function<T, Future<T>>> aopFn,
        final Function<T, Future<T>> executor) {
        return input -> {
            final JsonObject aop = module.getAop();
            if (Ut.isNil(aop)) {
                return executor.apply(input);
            } else {
                final Aspect aspect = Aspect.create(aop);
                return aopFn.apply(aspect, executor).apply(input);
            }
        };
    }
}
