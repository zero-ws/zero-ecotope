package io.zerows.epoch.component.serialization;

import io.zerows.epoch.program.Ut;

import java.util.function.Function;

/**
 * Int, Long, Short
 */
public abstract class AbstractNumericSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (this.isValid(paramType)) {
            this.verifyInput(!Ut.isInteger(literal), paramType, literal);
            return this.getFun().apply(literal);
        }
        return null;
    }

    protected abstract boolean isValid(final Class<?> paramType);

    protected abstract <T> Function<String, T> getFun();
}
