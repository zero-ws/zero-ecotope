package io.zerows.support.base;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.WebException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.zerows.platform.exception._11011Exception500InvokingPre;
import io.zerows.platform.exception._60059Exception412ArgumentNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Call interface method by cglib
 */
@Slf4j
@SuppressWarnings("unchecked")
final class UInvoker {
    private UInvoker() {
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

        /* 提取方法引用 */
        Method method = null;
        try {
            method = methodSeek(instance, name, args);
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
        }

        /* 方法检查 */
        if (Objects.isNull(method)) {
            throw new _60059Exception412ArgumentNull("method: " + name + " is null");
        }

        final Class<?> returnType = method.getReturnType();
        // 同步调用
        Object result;
        try {
            result = method.invoke(instance, args);
        } catch (final Throwable ex) {
            final WebException error = FnVertx.failAt(ex);
            if (Future.class.isAssignableFrom(returnType)) {
                // 异步调用
                result = Future.failedFuture(error);
            } else {
                /*
                 * Fix: Sync Calling
                 * 修复同步调用出现异常的问题，如果是异步调用，返回 Future.failedFuture(error)，而
                 * 同步调用则直接抛出异常即可。
                 */
                throw error;
            }
        }
        return (T) result;
    }

    static <T> Future<T> invokeAsync(final Object instance,
                                     final Method method,
                                     final Object... args) {
        /*
         * 首先分析方法的返回类型
         */
        final Class<?> returnType = method.getReturnType();
        try {
            /*
             * 针对 void 返回类型的方法，通常用于延续调用
             */
            if (void.class == returnType) {
                /*
                 * 当返回类型为 void 时，必须保证最后一个参数是 Promise 或 Future
                 * 参数列表：Arguments [] + Future<T> future
                 *
                 * 关键点：
                 * -- 如果方法返回 void，则意味着该方法是一个异步回调模式的方法。
                 * -- 调用方必须提供一个 Future/Promise 类型的参数，并且将其放在参数列表的末尾。
                 * -- 基本条件：方法声明的参数数量 == 传入参数数量 + 1
                 */
                Fn.jvmKo(method.getParameters().length != args.length + 1,
                    _11011Exception500InvokingPre.class, method);
                /*
                 * void 返回类型，系统自动追加最后一个 promise 参数
                 */
                final Promise<T> promise = Promise.promise();
                final Object[] arguments = CAdd.add(args, promise.future());
                method.invoke(instance, arguments);
                return promise.future();
            } else {
                final Object returnValue = method.invoke(instance, args);
                if (Objects.isNull(returnValue)) {
                    /*
                     * 返回值为 null，直接返回 Future.succeededFuture(null)
                     * 这种情况不使用 promise
                     */
                    return Future.succeededFuture(null);
                } else {
                    /*
                     * 异步工作流调用的关键点，Future 编排逻辑的核心
                     * 代码编程模式下，这是 Future compose 的关键问题
                     */
                    if (isEqualAnd(returnType, Future.class)) {
                        /*
                         * 直接返回 Future<T>，连接 Future -> Future
                         * 因为 Future 已经是方法的返回值，这里可以直接返回内部的 Future
                         * 替换原有的 returnValue
                         */
                        return ((Future<T>) returnValue);
                    } else if (isEqualAnd(returnType, AsyncResult.class)) {
                        /*
                         * 返回 AsyncResult
                         */
                        final AsyncResult<T> async = (AsyncResult<T>) returnValue;
                        final Promise<T> promise = Promise.promise();
                        promise.handle(async);
                        return promise.future();
                    } else if (isEqualAnd(returnType, Handler.class)) {
                        /*
                         * 返回 Handler，暂未严格测试
                         * 连接 future 到 handler
                         */
                        return ((Future<T>) returnValue);
                    } else {
                        /*
                         * 同步调用结果
                         * 直接将返回值包装成 Future.succeededFuture
                         */
                        final T returnT = (T) returnValue;
                        return Future.succeededFuture(returnT);
                    }
                }
            }
        } catch (final Throwable ex) {
            return Future.failedFuture(FnVertx.failAt(ex));
        }
        // 旧代码，不可达区域
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
