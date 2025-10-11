package io.zerows.component.log;

import io.r2mo.typed.cc.Cc;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author lang : 2023/4/25
 */
public class LogModule {
    private static final Cc<String, LogModule> CC_LOG_EXTENSION = Cc.openThread();
    private final String module;
    private String type;
    private Function<String, String> colorFn;

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

    private String format(final String pattern) {
        return " [ " + (Objects.isNull(this.colorFn) ? this.module : this.colorFn.apply(this.module)) + " ] "
            + " ( " + this.type + " ) " + pattern;
    }

    public void info(final Class<?> clazz, final String pattern, final Object... args) {
        LogOf.get(clazz).info(this.format(pattern), args);
    }

    public void info(final boolean condition, final Class<?> clazz, final String pattern, final Object... args) {
        LogOf.get(clazz).info(condition, this.format(pattern), args);
    }

    public void debug(final Class<?> clazz, final String pattern, final Object... args) {
        LogOf.get(clazz).debug(this.format(pattern), args);
    }

    public void warn(final Class<?> clazz, final String pattern, final Object... args) {
        LogOf.get(clazz).warn(this.format(pattern), args);
    }

    public void error(final Class<?> clazz, final String pattern, final Object... args) {
        LogOf.get(clazz).error(this.format(pattern), args);
    }

    public void fatal(final Class<?> clazz, final Throwable ex) {
        LogOf.get(clazz).fatal(ex);
    }

    public void fatal(final Class<?> clazz, final Throwable ex, final String prefix) {
        LogOf.get(clazz).fatal(ex, prefix);
    }

    // ---------------------- 外层构造 ------------------
    public void info(final LogOf logger, final String pattern, final Object... args) {
        final LogOf annal = Log.logger(logger);
        annal.info(this.format(pattern), args);
    }

    public void info(final boolean condition, final LogOf logger, final String pattern, final Object... args) {
        final LogOf annal = Log.logger(logger);
        annal.info(condition, this.format(pattern), args);
    }

    public void debug(final LogOf logger, final String pattern, final Object... args) {
        final LogOf annal = Log.logger(logger);
        annal.debug(this.format(pattern), args);
    }

    public void warn(final LogOf logger, final String pattern, final Object... args) {
        final LogOf annal = Log.logger(logger);
        annal.warn(this.format(pattern), args);
    }

    public void error(final LogOf logger, final String pattern, final Object... args) {
        final LogOf annal = Log.logger(logger);
        annal.error(this.format(pattern), args);
    }

    public void fatal(final LogOf logger, final Throwable ex) {
        final LogOf annal = Log.logger(logger);
        annal.fatal(ex);
    }

    public void fatal(final LogOf logger, final Throwable ex, final String prefix) {
        final LogOf annal = Log.logger(logger);
        annal.fatal(ex, prefix);
    }
}
