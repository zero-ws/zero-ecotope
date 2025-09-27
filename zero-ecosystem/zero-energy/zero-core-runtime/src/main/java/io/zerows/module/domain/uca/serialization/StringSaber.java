package io.zerows.module.domain.uca.serialization;

import io.zerows.ams.constant.VString;

/**
 * String
 */
class StringSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return String.class == paramType ? literal : VString.EMPTY;
    }
}
