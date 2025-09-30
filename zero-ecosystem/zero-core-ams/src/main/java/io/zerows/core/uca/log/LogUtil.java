package io.zerows.core.uca.log;

import io.zerows.ams.util.HUt;
import io.zerows.core.running.HMacrocosm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lang : 2024-06-17
 */
public class LogUtil {
    private final Logger logger;

    private LogUtil(final Class<?> target) {
        this.logger = LoggerFactory.getLogger(target);
    }

    public static LogUtil from(final Class<?> target) {
        return new LogUtil(target);
    }

    public void io(final String pattern, final Object... args) {
        /* 底层防止循环调用，此处不走 DiagnosisOption */
        final String value = System.getenv(HMacrocosm.DEV_IO);
        if (HUt.isBoolean(value)) {
            final boolean ioDebug = Boolean.parseBoolean(value);
            if (ioDebug) {
                final String message = HUt.fromMessage(pattern, args);
                this.logger.info(message);
            }
        }
    }

    public void info(final String pattern, final Object... args) {
        final String message = HUt.fromMessage(pattern, args);
        this.logger.info(message);
    }

    public void warn(final String pattern, final Object... args) {
        final String message = HUt.fromMessage(pattern, args);
        this.logger.warn(message);
    }

    public void error(final String pattern, final Object... args) {
        final String message = HUt.fromMessage(pattern, args);
        this.logger.error(message);
    }

    public void debug(final String pattern, final Object... args) {
        final String message = HUt.fromMessage(pattern, args);
        this.logger.debug(message);
    }
}
