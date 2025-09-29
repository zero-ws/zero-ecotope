package io.zerows.module.domain.uca.serialization;

import io.zerows.core.fn.FnZero;
import io.zerows.core.util.Ut;
import io.zerows.module.domain.exception._400ParameterFromStringException;
import io.zerows.module.metadata.uca.logging.OLog;

public abstract class AbstractSaber implements Saber {

    protected OLog logger() {
        return Ut.Log.uca(this.getClass());
    }

    void verifyInput(final boolean condition,
                     final Class<?> paramType,
                     final String literal) {
        FnZero.outWeb(condition,
            this.logger(), _400ParameterFromStringException.class,
            this.getClass(), paramType, literal);
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
