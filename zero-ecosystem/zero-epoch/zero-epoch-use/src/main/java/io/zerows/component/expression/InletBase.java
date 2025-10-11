package io.zerows.component.expression;

import io.zerows.component.environment.DevEnv;
import io.zerows.component.log.LogO;
import io.zerows.support.Ut;

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
        if (DevEnv.devExprBind()) {
            final LogO logger = Ut.Log.plugin(this.getClass());
            logger.info(message, args);
        }
    }
}
