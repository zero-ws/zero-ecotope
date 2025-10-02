package io.zerows.epoch.component.serialization;

import io.zerows.epoch.program.Ut;

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
