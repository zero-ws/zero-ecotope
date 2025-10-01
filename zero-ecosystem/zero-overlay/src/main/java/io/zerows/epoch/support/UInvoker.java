package io.zerows.epoch.support;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.zerows.epoch.exception.boot._11011Exception500InvokingPre;
import io.zerows.epoch.exception.web._60059Exception412ArgumentNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Call interface method by cglib
 */
@SuppressWarnings("unchecked")
final class UInvoker {
    private UInvoker() {
    }

    static <T> T invokeStatic(
        final Class<?> clazz,
        final String name,
        final Object... args
    ) {
        Objects.requireNonNull(clazz);
        final Method[] methods = clazz.getDeclaredMethods();
        Method found = null;
        for (final Method method : methods) {
            if (name.equals(method.getName())) {
                found = method;
                break;
            }
        }
        if (Objects.isNull(found) || !Modifier.isStatic(found.getModifiers())) {
            return null;
        }
        final Method invoker = found;
        return (T) Fn.jvmOr(() -> invoker.invoke(null, args));
    }

    private static Method methodSeek(final Object instance, final String name, final Object... args) {
        // Direct invoke, multi overwrite for unbox/box issue still existing.
        if (TIs.isNil(name) || Objects.isNull(instance)) {
            throw new _60059Exception412ArgumentNull("name | instance");
        }
        final Class<?> clazz = instance.getClass();
        final List<Class<?>> types = new ArrayList<>();
        for (final Object arg : args) {
            if (Objects.isNull(arg)) {
                types.add(null);
            } else {
                types.add(TTo.toPrimary(arg.getClass()));
            }
        }
        final Class<?>[] arguments = types.toArray(new Class<?>[]{});
        final Method[] methods = clazz.getMethods();
        Method method = null;
        for (final Method hit : methods) {
            if (isMatch(hit, name, arguments)) {
                method = hit;
                break;
            }
        }
        return method;
    }

    static <T> T invokeObject(
        final Object instance,
        final String name,
        final Object... args) {

        /* Method Extracting */
        Method method = null;
        try {
            method = methodSeek(instance, name, args);
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }

        /* Method checking */
        if (Objects.isNull(method)) {
            throw new _60059Exception412ArgumentNull("method: " + name + " is null");
        }

        final Class<?> returnType = method.getReturnType();
        // Sync Calling
        Object result;
        try {
            result = method.invoke(instance, args);
        } catch (final Throwable ex) {
            ex.printStackTrace();
            if (Future.class.isAssignableFrom(returnType)) {
                // Async Calling
                final WebException error = new _500ServerInternalException("[ R2MO ] 调用过程中的其他异常：" + ex.getMessage());
                result = Future.failedFuture(error);
            } else {
                // Sync Calling
                result = null;
            }
        }
        return (T) result;
    }

    static <T> Future<T> invokeAsync(final Object instance,
                                     final Method method,
                                     final Object... args) {
        /*
         * Analyzing method returnType first
         */
        final Class<?> returnType = method.getReturnType();
        try {
            /*
             * Void return for continue calling
             */
            if (void.class == returnType) {
                /*
                 * When void returned, here you must set the last argument to Future<T>
                 * Arguments [] + Future<T> future
                 *
                 * Critical:
                 * -- It means that if you want to return void.class, you must provide
                 *    argument future and let the future as last arguments
                 * -- The basic condition is
                 *    method declared length = args length + 1
                 */
                Fn.jvmKo(method.getParameters().length != args.length + 1,
                    _11011Exception500InvokingPre.class, method);
                /*
                 * void.class, the system should provide secondary parameters
                 */
                final Promise<T> promise = Promise.promise();
                final Object[] arguments = CAdd.add(args, promise.future());
                method.invoke(instance, arguments);
                return promise.future();
            } else {
                final Object returnValue = method.invoke(instance, args);
                if (Objects.isNull(returnValue)) {
                    /*
                     * Future also null
                     * Return to Future.succeededFuture with null stored
                     * instead of use promise here.
                     */
                    return Future.succeededFuture(null);
                } else {
                    /*
                     * Workflow async invoking issue here for
                     * Code programming, it's very critical issue of Future compose
                     */
                    if (isEqualAnd(returnType, Future.class)) {
                        /*
                         * Future<T> returned directly,
                         * Connect future -> future
                         * Return to Future directly, because future is method
                         * return value, here, we could return internal future directly
                         * Replaced with method returnValue
                         */
                        return ((Future<T>) returnValue);
                    } else if (isEqualAnd(returnType, AsyncResult.class)) {
                        /*
                         * AsyncResult
                         */
                        final AsyncResult<T> async = (AsyncResult<T>) returnValue;
                        final Promise<T> promise = Promise.promise();
                        promise.handle(async);
                        return promise.future();
                    } else if (isEqualAnd(returnType, Handler.class)) {
                        /*
                         * Handler, not testing here.
                         * Connect future to handler
                         * Old code
                         * promise.future().setHandler(((Handler<AsyncResult<T>>) returnValue));
                         */
                        return ((Future<T>) returnValue);
                    } else {
                        /*
                         * Sync calling
                         * Wrapper future with T instance directly
                         */
                        final T returnT = (T) returnValue;
                        return Future.succeededFuture(returnT);
                    }
                }
            }
        } catch (final Throwable ex) {
            ex.printStackTrace();
            return Future.failedFuture(ex);
        }
        // Old code set to un-reach code here
        // return promise.future();
    }

    private static boolean isEqualAnd(final Class<?> clazz, final Class<?> interfaceCls) {
        return clazz == interfaceCls || UtBase.isImplement(clazz, interfaceCls);
    }

    private static boolean isMatch(final Method method, final String name, final Class<?>[] arguments) {
        if (!name.equals(method.getName())) {
            // Name not match
            return false;
        }
        final Class<?>[] parameters = method.getParameterTypes();
        if (arguments.length != parameters.length) {
            // Argument length not match
            return false;
        }
        boolean allMatch = true;
        for (int idx = 0; idx < parameters.length; idx++) {
            Class<?> argument = arguments[idx];
            if (Objects.isNull(argument)) {
                continue;
            }
            final Class<?> parameter = TTo.toPrimary(parameters[idx]);
            argument = TTo.toPrimary(arguments[idx]);
            // First situation equal
            if (!isMatch(parameter, argument)) {
                allMatch = false;
                break;
            }
        }
        return allMatch;
    }

    private static boolean isMatch(final Class<?> parameterIdx, final Class<?> argumentIdx) {
        final Class<?> parameter = TTo.toPrimary(parameterIdx);
        final Class<?> argument = TTo.toPrimary(argumentIdx);
        if (argument == parameter) {
            return true;
        }
        return parameter.isAssignableFrom(argument);
    }
}
