package io.zerows.weaver;

import io.r2mo.function.Fn;
import io.zerows.epoch.exception._60004Exception400ParamFromString;
import io.zerows.component.log.OLog;
import io.zerows.support.Ut;

public abstract class SaberBase implements Saber {

    protected OLog logger() {
        return Ut.Log.uca(this.getClass());
    }

    void verifyInput(final boolean condition,
                     final Class<?> paramType,
                     final String literal) {
        Fn.jvmKo(condition, _60004Exception400ParamFromString.class, paramType, literal);
    }

    @Override
    public <T> Object from(final T input) {
        // Default direct
        return input;
    }

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        // Default direct
        return literal;
    }
}
