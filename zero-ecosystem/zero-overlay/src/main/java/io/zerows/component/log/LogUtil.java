package io.zerows.component.log;

import io.zerows.platform.HEnvironmentVariable;
import io.zerows.support.base.UtBase;
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
        final String value = System.getenv(HEnvironmentVariable.DEV_IO);
        if (UtBase.isBoolean(value)) {
            final boolean ioDebug = Boolean.parseBoolean(value);
            if (ioDebug) {
                final String message = UtBase.fromMessage(pattern, args);
                this.logger.info(message);
            }
        }
    }

    public void info(final String pattern, final Object... args) {
        final String message = UtBase.fromMessage(pattern, args);
        this.logger.info(message);
    }

    public void warn(final String pattern, final Object... args) {
        final String message = UtBase.fromMessage(pattern, args);
        this.logger.warn(message);
    }

    public void error(final String pattern, final Object... args) {
        final String message = UtBase.fromMessage(pattern, args);
        this.logger.error(message);
    }

    public void debug(final String pattern, final Object... args) {
        final String message = UtBase.fromMessage(pattern, args);
        this.logger.debug(message);
    }
}
