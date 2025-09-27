package io.zerows.module.feature.toolkit.expression.script;

import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.environment.DevEnv;
import io.zerows.module.metadata.uca.logging.OLog;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractInlet implements Inlet {

    protected transient boolean isPrefix;

    protected AbstractInlet(final boolean isPrefix) {
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
            final OLog logger = Ut.Log.plugin(this.getClass());
            logger.info(message, args);
        }
    }
}
