package io.zerows.epoch.testsuite;

import io.zerows.epoch.assembly.DI;
import io.zerows.epoch.database.jooq.JooqInfix;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class JooqBase extends ZeroBase {
    static {
        JooqInfix.init(ZeroBase.VERTX);
    }

    protected <T> T component(final Class<?> clazz) {
        final DI plugin = DI.create(this.getClass());
        return plugin.createSingleton(clazz);
    }
}
