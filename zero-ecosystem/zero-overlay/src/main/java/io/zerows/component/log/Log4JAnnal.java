package io.zerows.component.log;

import io.r2mo.typed.exception.AbstractException;
import io.zerows.constant.VString;
import io.zerows.runtime.HMacrocosm;
import io.zerows.support.UtBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


public class Log4JAnnal implements Annal {

    private transient final Logger logger;

    public Log4JAnnal(final Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            this.logger = LoggerFactory.getLogger(Log4JAnnal.class);
        } else {
            this.logger = LoggerFactory.getLogger(clazz);
        }
    }

    public Log4JAnnal(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void fatal(final Throwable ex, final String prefix) {
        Objects.requireNonNull(ex);
        final String message = UtBase.isNil(prefix) ? ex.getMessage() :
            prefix + VString.SLASH + ex.getMessage();
        if (ex instanceof AbstractException) {
            this.logger.warn(message);
        } else {
            this.logger.error(message);
        }
        final Boolean isDebug = UtBase.envWith(HMacrocosm.DEV_JVM_STACK, Boolean.FALSE, Boolean.class);
        if (isDebug) {
            /*
             * 堆栈信息打印条件
             * 1. 开启了 DEV_JVM_STACK 环境变量
             * 2. 传入异常不可为空
             */
            ex.printStackTrace();
        }
    }

    private void log(final Supplier<Boolean> fnPre,
                     final BiConsumer<String, Object> fnLog,
                     final String message,
                     final Object... rest) {
        if (fnPre.get()) {
            final String formatted = UtBase.fromMessageB(message, rest);
            fnLog.accept(formatted, null);
        }
    }

    @Override
    public void warn(final String key, final Object... args) {
        this.log(this.logger::isWarnEnabled, this.logger::warn, key, args);
    }

    @Override
    public void error(final String key, final Object... args) {
        this.log(this.logger::isErrorEnabled, this.logger::error, key, args);
    }


    @Override
    public void info(final String key, final Object... args) {
        this.log(this.logger::isInfoEnabled, this.logger::info, key, args);
    }

    @Override
    public void info(final boolean condition, final String key, final Object... args) {
        this.log(() -> condition && this.logger.isInfoEnabled(), this.logger::info, key, args);
    }

    @Override
    public void debug(final String key, final Object... args) {
        this.log(this.logger::isDebugEnabled, this.logger::debug, key, args);
    }
}
