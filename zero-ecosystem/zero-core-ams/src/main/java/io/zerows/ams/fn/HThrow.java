package io.zerows.ams.fn;

import io.vertx.core.Future;
import io.zerows.ams.util.HUt;
import io.zerows.core.exception.BootingException;
import io.zerows.core.exception.InternalException;
import io.zerows.core.exception.WebException;
import io.zerows.core.exception.web._412ArgumentNullException;
import io.zerows.core.exception.web._500InternalServerException;
import io.zerows.core.running.HMacrocosm;
import io.zerows.core.uca.log.Annal;
import io.zerows.specification.atomic.HLogger;

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
            failure = new _500InternalServerException(target, "Otherwise Web Error without Throwable!");
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
                failure = new _500InternalServerException(target, error.getMessage());
            }
        }
        return Future.failedFuture(failure);
    }

    /*
     * - WebException
     * - BootingException
     */
    static void out(final Class<?> errorCls, final Object... args) {
        if (BootingException.class == errorCls.getSuperclass()) {
            final BootingException error = HUt.instance(errorCls, args);
            if (null != error) {
                callerAt(error::caller, error::getMessage);
                throw error;
            }
        } else if (WebException.class == errorCls.getSuperclass()) {
            final WebException error = HUt.instance(errorCls, args);
            if (null != error) {
                callerAt(error::caller, error::getMessage);
                throw error;
            }
        }
    }

    static void outInternal(final Class<?> errorCls, final Object... args) {
        if (InternalException.class == errorCls.getSuperclass()) {
            final InternalException error = HUt.instance(errorCls, args);
            if (null != error) {
                callerAt(error::caller, error::getMessage);
                throw error;
            }
        }
    }

    static void outWeb(final Class<? extends WebException> webClass,
                       final Object... args) {
        final WebException error = HUt.instance(webClass, args);
        if (null != error) {
            callerAt(error::caller, error::getMessage);
            throw error;
        }
    }

    static void outWeb(final HLogger logger,
                       final Class<? extends WebException> webClass,
                       final Object... args) {
        final WebException error = HUt.instance(webClass, args);
        if (null != error) {
            if (Objects.nonNull(logger)) {
                logger.warn(error.getMessage());
            }
            throw error;
        }
    }

    static void outBoot(final HLogger logger,
                        final Class<? extends BootingException> upClass,
                        final Object... args) {
        final BootingException error = HUt.instance(upClass, args);
        if (null != error) {
            if (Objects.nonNull(logger)) {
                logger.fatal(error);
            }
            throw error;
        }
    }

    static void outBoot(final Class<? extends BootingException> upClass,
                        final Object... args) {
        final BootingException error = HUt.instance(upClass, args);
        if (null != error) {
            callerAt(error::caller, error::getMessage);
            throw error;
        }
    }

    static <T> void outArg(final T condition, final Class<?> clazz, final String message) {
        if (condition instanceof final Boolean check) {
            // If boolean, condition = true, throw Error
            if (check) {
                outWeb(_412ArgumentNullException.class, clazz, message);
            }
        } else if (condition instanceof final String check) {
            // If string, condition = empty or null, throw Error
            if (HUt.isNil(check)) {
                outWeb(_412ArgumentNullException.class, clazz, message);
            }
        } else if (Objects.isNull(condition)) {
            // If object, condition = null, throw Error
            outWeb(_412ArgumentNullException.class, clazz, message);
        }
    }

    private static void callerAt(final Supplier<Class<?>> supplier, final Supplier<String> message) {
        final Class<?> target = supplier.get();
        if (null != target) {
            final Annal logger = Annal.get(target);
            logger.warn(message.get());
        }
    }
}
