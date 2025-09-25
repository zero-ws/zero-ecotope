package io.zerows.module.domain.uca.serialization;

import io.zerows.core.util.Ut;

import java.util.function.Function;

/**
 * Double, Float, BigDecimal
 */
public abstract class AbstractDecimalSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (this.isValid(paramType)) {
            this.verifyInput(!Ut.isDecimal(literal), paramType, literal);
            return this.getFun().apply(literal);
        }
        return 0.0;
    }

    protected abstract boolean isValid(final Class<?> paramType);

    protected abstract <T> Function<String, T> getFun();
}
