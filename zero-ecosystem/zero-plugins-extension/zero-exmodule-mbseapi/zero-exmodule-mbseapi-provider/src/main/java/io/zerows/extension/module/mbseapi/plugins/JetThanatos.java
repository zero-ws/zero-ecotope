package io.zerows.extension.module.mbseapi.plugins;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.exception._80403Exception400RequiredParam;
import io.zerows.extension.module.mbseapi.exception._80406Exception500DefinitionError;

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
        // return Fn.po?l(POOL.ENSURERS, ofMain, () -> new JetThanatos(ofMain));
    }

    public Envelop to400RequiredParam(final String filename) {
        return Envelop.failure(new _80403Exception400RequiredParam(filename));
    }

    public Envelop to500DefinitionError(final String key) {
        return Envelop.failure(new _80406Exception500DefinitionError(key));
    }
}
