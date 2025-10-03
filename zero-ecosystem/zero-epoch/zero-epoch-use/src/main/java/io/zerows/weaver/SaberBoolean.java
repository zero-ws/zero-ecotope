package io.zerows.weaver;

import io.zerows.support.Ut;

/**
 * Boolean
 */
class SaberBoolean extends SaberBase {

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
