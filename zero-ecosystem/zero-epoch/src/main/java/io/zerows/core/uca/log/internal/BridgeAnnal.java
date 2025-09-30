package io.zerows.core.uca.log.internal;

import io.zerows.ams.util.HUt;
import io.zerows.core.spi.HorizonIo;
import io.zerows.core.uca.log.Annal;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023/4/25
 */
public class BridgeAnnal extends AbstractAnnal implements Annal {
    /*
     * LoggerFactory.getLogger
     * Default implementation for Logger here, the logger of zero framework is
     * logback instead of log4j / log4j2, this is common usage.
     */
    private static final ConcurrentMap<Class<?>, Annal> OUTED = new ConcurrentHashMap<>();
    private transient final Annal logger;

    public BridgeAnnal(final Class<?> clazz, final HorizonIo io) {
        Class<?> inject = Objects.isNull(io) ? null : io.ofLogger();
        if (null == inject) {
            inject = Log4JAnnal.class;
        }
        final Class<?> cacheKey = inject;
        this.logger = OUTED.computeIfAbsent(clazz, (key) -> HUt.instance(cacheKey, key));
    }

    public BridgeAnnal(final Annal logger) {
        this.logger = logger;
    }

    @Override
    public void warn(final String key, final Object... args) {
        this.logger.warn(key, args);
    }

    @Override
    public void error(final String key, final Object... args) {
        this.logger.error(key, args);
    }

    @Override
    public void fatal(final Throwable ex, final String prefix) {
        this.logger.fatal(ex, prefix);
    }

    @Override
    public void info(final String key, final Object... args) {
        this.logger.info(key, args);
    }

    @Override
    public void info(final boolean condition, final String key, final Object... args) {
        this.logger.info(condition, key, args);
    }

    @Override
    public void debug(final String key, final Object... args) {
        this.logger.debug(key, args);
    }
}
