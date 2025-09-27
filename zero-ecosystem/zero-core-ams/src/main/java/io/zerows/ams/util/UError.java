package io.zerows.ams.util;

import io.zerows.core.exception.WebException;
import io.zerows.core.exception.web._500InternalCauseException;
import io.zerows.core.exception.web._500InternalServerException;

import java.util.Objects;

/**
 * @author lang : 2023/4/30
 */
class UError {

    // 异常专用信息
    static WebException failWeb(final Class<?> clazz, final Throwable error,
                                final boolean isCause) {
        if (error instanceof WebException) {
            return (WebException) error;
        }
        final Class<?> target = Objects.isNull(clazz) ? UError.class : clazz;
        // 传入 Throwable 是否为空
        if (Objects.isNull(error)) {
            return new _500InternalServerException(target, "Throwable is null");
        }
        if (isCause) {
            // 调用 getCause() 模式
            final Throwable cause = error.getCause();
            if (Objects.isNull(cause)) {
                return new _500InternalCauseException(target, error);
            }

            // 递归调用
            return failWeb(clazz, cause, true);
        } else {
            // 直接模式
            return new _500InternalServerException(target, error.getMessage());
        }
    }

    static WebException failWeb(final Class<? extends WebException> clazz, final Object... args) {
        // Fix：此处必须追加 <WebException> 泛型，否则会抛出转型异常
        if (Objects.isNull(clazz)) {
            // 特殊情况，编程过程中忘了传入 clazz
            return new _500InternalServerException(clazz, "WebException class is null");
        }
        // 正常情况，传入 clazz
        return UInstance.instance(clazz, args);
    }
}
