package io.zerows.epoch.testsuite;

import io.zerows.epoch.corpus.assembly.uca.di.DiPlugin;
import io.zerows.epoch.corpus.database.jooq.JooqInfix;

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
