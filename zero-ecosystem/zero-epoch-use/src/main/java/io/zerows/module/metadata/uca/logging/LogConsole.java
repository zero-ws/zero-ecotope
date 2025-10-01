package io.zerows.module.metadata.uca.logging;

import io.zerows.core.uca.log.Log;
import io.zerows.core.uca.log.LogModule;

/**
 * @author lang : 2024-04-17
 */
class LogConsole implements OLog {
    private static final String MODULE = "Zero";

    private final transient LogModule internalLogger;
    private final transient Class<?> targetClass;

    LogConsole(final Class<?> clazz, final String name) {
        this.targetClass = clazz;

        this.internalLogger = Log.modulat(MODULE).osgi(name);
    }

    @Override
    public void info(final String pattern, final Object... args) {
        this.internalLogger.info(this.targetClass, pattern, args);
    }

    @Override
    public void debug(final String pattern, final Object... args) {
        this.internalLogger.debug(this.targetClass, pattern, args);
    }

    @Override
    public void warn(final String pattern, final Object... args) {
        this.internalLogger.warn(this.targetClass, pattern, args);
    }

    @Override
    public void error(final String pattern, final Object... args) {
        this.internalLogger.error(this.targetClass, pattern, args);
    }

    @Override
    public void fatal(final Throwable ex) {
        this.internalLogger.fatal(this.targetClass, ex);
    }

    @Override
    public void fatal(final Throwable ex, final String prefix) {
        this.internalLogger.fatal(this.targetClass, ex, prefix);
    }
}
