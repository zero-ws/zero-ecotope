package io.zerows.core.testing;

import io.zerows.core.database.jooq.JooqInfix;
import io.zerows.module.assembly.uca.di.DiPlugin;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class JooqBase extends ZeroBase {
    static {
        JooqInfix.init(ZeroBase.VERTX);
    }

    protected <T> T component(final Class<?> clazz) {
        final DiPlugin plugin = DiPlugin.create(this.getClass());
        return plugin.createSingleton(clazz);
    }
}
