package io.zerows.ams.fn;

import io.zerows.core.exception.BootingException;
import io.zerows.core.exception.WebException;
import io.zerows.specification.atomic.HLogger;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023/4/28
 */
class _Out extends _Combine {
    protected _Out() {
    }

    /**
     * 检查条件，如果满足条件则抛出 BootingException 异常，且支持传入 HLogger 日志记录器
     * 如果日志记录器不为空，则以日志记录器记录异常信息
     *
     * @param condition 条件
     * @param logger    日志记录器
     * @param bootClass BootingException 异常类
     * @param args      异常参数
     */
    @Deprecated
    public static void outBoot(final boolean condition, final HLogger logger,
                               final Class<? extends BootingException> bootClass, final Object... args) {
        if (condition) {
            HThrow.outBoot(logger, bootClass, args);
        }
    }

    /**
     * 检查条件，如果满足条件则抛出 BootingException 异常
     *
     * @param condition 条件
     * @param bootClass BootingException 异常类
     * @param args      异常参数
     */
    @Deprecated
    public static void outBoot(final boolean condition,
                               final Class<? extends BootingException> bootClass, final Object... args) {
        if (condition) {
            HThrow.outBoot(bootClass, args);
        }
    }

    /**
     * 检查条件，如果满足条件则抛出 WebException 异常，且支持传入 HLogger 日志记录器
     * 如果日志记录器不为空，则以日志记录器记录异常信息
     *
     * @param condition 条件
     * @param logger    日志记录器
     * @param webClass  WebException 异常类
     * @param args      异常参数
     */
    @Deprecated
    public static void outWeb(final boolean condition, final HLogger logger,
                              final Class<? extends WebException> webClass, final Object... args) {
        if (condition) {
            HThrow.outWeb(logger, webClass, args);
        }
    }

    /**
     * 检查条件，如果满足条件则抛出 WebException 异常
     *
     * @param condition 条件
     * @param webClass  WebException 异常类
     * @param args      异常参数
     */
    @Deprecated
    public static void outWeb(final boolean condition,
                              final Class<? extends WebException> webClass, final Object... args) {
        if (condition) {
            HThrow.outWeb(webClass, args);
        }
    }

    /**
     * 防御式编程专用方法，参数检查（Web模式抛出）
     * 1. 如果 condition 为 true，则直接抛出 _412NullValueException
     * 2. 如果是 Future 模式，则返回异步结果
     * 默认消息如下:
     * 1. ArgNull -> [ Program ] Null Input
     * 2. ArgQr -> [ Program ] Null Record in database
     *
     * @param condition 条件
     * @param clazz     类
     * @param message   消息
     * @param <T>       泛型
     */
    @Deprecated
    public static <T> void outArg(final T condition, final Class<?> clazz, final String message) {
        HThrow.outArg(condition, clazz, message);
    }

    /**
     * 条件满足后抛出对应异常 {@link BootingException} 和 {@link WebException}
     *
     * @param condition 满足抛异常的条件
     * @param clazz     异常类
     * @param args      反射参数
     */
    @Deprecated
    public static void out(final boolean condition, final Class<?> clazz, final Object... args) {
        if (condition) {
            HThrow.out(clazz, args);
        }
    }

    @Deprecated
    public static <T> Function<Throwable, T> outAsync(final Supplier<T> supplier) {
        return HThrow.outAsync(supplier);
    }
}
