package io.zerows.epoch.component.serialization;

import io.r2mo.function.Fn;
import io.zerows.epoch.based.exception._60004Exception400ParamFromString;
import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.program.Ut;

public abstract class AbstractSaber implements Saber {

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
