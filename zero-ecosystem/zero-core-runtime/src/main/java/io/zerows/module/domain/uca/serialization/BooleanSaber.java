package io.zerows.module.domain.uca.serialization;

import io.zerows.core.util.Ut;

/**
 * Boolean
 */
class BooleanSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (boolean.class == paramType || Boolean.class == paramType) {
            this.verifyInput(!Ut.isBoolean(literal), paramType, literal);
            return Boolean.parseBoolean(literal);
        }
        return Boolean.FALSE;
    }
}
