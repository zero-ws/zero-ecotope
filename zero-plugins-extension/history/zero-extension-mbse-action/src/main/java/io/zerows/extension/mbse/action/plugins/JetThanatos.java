package io.zerows.extension.mbse.action.plugins;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.mbse.action.exception._400RequiredParamException;
import io.zerows.extension.mbse.action.exception._500DefinitionErrorException;

/*
 * Uniform Error throw out and web Envelop ( Failure )
 * Define vertx-readible.yml for ui message.
 */
public class JetThanatos {

    private static final Cc<Class<?>, JetThanatos> CC_ENSURER = Cc.open();
    private transient final Class<?> target;

    private JetThanatos(final Class<?> target) {
        this.target = target;
    }

    public static JetThanatos create(final Class<?> target) {
        return CC_ENSURER.pick(() -> new JetThanatos(target), target);
        // return Fn.po?l(POOL.ENSURERS, target, () -> new JetThanatos(target));
    }

    public Envelop to400RequiredParam(final String filename) {
        return Envelop.failure(new _400RequiredParamException(this.target, filename));
    }

    public Envelop to500DefinitionError(final String key) {
        return Envelop.failure(new _500DefinitionErrorException(this.target, key));
    }
}
