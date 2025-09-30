package io.zerows.ams.fn;

import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.Future;
import io.zerows.ams.util.HUt;
import io.zerows.core.running.HMacrocosm;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023/4/28
 */
class HThrow {

    @SuppressWarnings("all")
    static <T> Function<Throwable, T> outAsync(final Supplier<T> supplier) {
        return error -> {
            if (Objects.nonNull(error)) {
                error.printStackTrace();
            }
            return supplier.get();
        };
    }

    @SuppressWarnings("all")
    static <T> Future<T> outAsync(final Class<?> target, final Throwable error) {
        final WebException failure;
        if (Objects.isNull(error)) {
            // 异常为 null
            failure = new _500ServerInternalException("[ R2MO ] 其他 Web 异常没有传入 Throwable！");
        } else {

            final Boolean isDebug = HUt.envWith(HMacrocosm.DEV_JVM_STACK, Boolean.FALSE, Boolean.class);
            if (isDebug) {
                error.printStackTrace();
            }
            if (error instanceof WebException) {
                // 异常为 WebException
                failure = (WebException) error;
            } else {
                // 其他异常，做 WebException 封装
                failure = new _500ServerInternalException("[ R2MO ] 其他 Web 异常：" + error.getMessage());
            }
        }
        return Future.failedFuture(failure);
    }
}
