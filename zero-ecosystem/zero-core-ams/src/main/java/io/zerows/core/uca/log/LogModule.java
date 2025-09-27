package io.zerows.core.uca.log;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.spi.HorizonIo;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023/4/25
 */
public class LogModule {
    private static final Cc<String, LogModule> CC_LOG_EXTENSION = Cc.openThread();
    private final String module;
    private String type;
    private Function<String, String> colorFn;

    private Supplier<HorizonIo> supplier;

    LogModule(final String module) {
        this.module = module;
    }

    public static LogModule instance(final String module) {
        return CC_LOG_EXTENSION.pick(() -> new LogModule(module), module);
    }

    public LogModule bind(final Function<String, String> colorFn) {
        synchronized (this) {
            this.colorFn = colorFn;
        }
        return this;
    }

    public LogModule bind(final String type) {
        synchronized (this) {
            this.type = type;
        }
        return this;
    }

    public LogModule io(final Supplier<HorizonIo> supplier) {
        synchronized (this) {
            this.supplier = supplier;
        }
        return this;
    }

    public LogModule io(final HorizonIo io) {
        synchronized (this) {
            this.supplier = () -> io;
        }
        return this;
    }

    private String format(final String pattern) {
        return " [ " + (Objects.isNull(this.colorFn) ? this.module : this.colorFn.apply(this.module)) + " ] "
            + " ( " + this.type + " ) " + pattern;
    }

    public void info(final Class<?> clazz, final String pattern, final Object... args) {
        this.wrapLogger(clazz).info(this.format(pattern), args);
    }

    public void info(final boolean condition, final Class<?> clazz, final String pattern, final Object... args) {
        this.wrapLogger(clazz).info(condition, this.format(pattern), args);
    }

    public void debug(final Class<?> clazz, final String pattern, final Object... args) {
        this.wrapLogger(clazz).debug(this.format(pattern), args);
    }

    public void warn(final Class<?> clazz, final String pattern, final Object... args) {
        this.wrapLogger(clazz).warn(this.format(pattern), args);
    }

    public void error(final Class<?> clazz, final String pattern, final Object... args) {
        this.wrapLogger(clazz).error(this.format(pattern), args);
    }

    public void fatal(final Class<?> clazz, final Throwable ex) {
        this.wrapLogger(clazz).fatal(ex);
    }

    public void fatal(final Class<?> clazz, final Throwable ex, final String prefix) {
        this.wrapLogger(clazz).fatal(ex, prefix);
    }

    // ---------------------- 外层构造 ------------------
    public void info(final Annal logger, final String pattern, final Object... args) {
        final Annal annal = Log.logger(logger);
        annal.info(this.format(pattern), args);
    }

    public void info(final boolean condition, final Annal logger, final String pattern, final Object... args) {
        final Annal annal = Log.logger(logger);
        annal.info(condition, this.format(pattern), args);
    }

    public void debug(final Annal logger, final String pattern, final Object... args) {
        final Annal annal = Log.logger(logger);
        annal.debug(this.format(pattern), args);
    }

    public void warn(final Annal logger, final String pattern, final Object... args) {
        final Annal annal = Log.logger(logger);
        annal.warn(this.format(pattern), args);
    }

    public void error(final Annal logger, final String pattern, final Object... args) {
        final Annal annal = Log.logger(logger);
        annal.error(this.format(pattern), args);
    }

    public void fatal(final Annal logger, final Throwable ex) {
        final Annal annal = Log.logger(logger);
        annal.fatal(ex);
    }

    public void fatal(final Annal logger, final Throwable ex, final String prefix) {
        final Annal annal = Log.logger(logger);
        annal.fatal(ex, prefix);
    }

    private Annal wrapLogger(final Class<?> clazz) {
        HorizonIo io = null;
        if (Objects.nonNull(this.supplier)) {
            io = this.supplier.get();
        }
        return Annal.get(clazz, io);
    }
}
