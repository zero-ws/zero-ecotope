package io.zerows.component.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class InletBase implements Inlet {

    protected transient boolean isPrefix;

    protected InletBase(final boolean isPrefix) {
        this.isPrefix = isPrefix;
    }

    protected String variable(final String name) {
        if (this.isPrefix) {
            return "$" + name;
        } else {
            return name;
        }
    }

    protected void console(final String message, final Object... args) {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.debug(message, args);
    }
}
