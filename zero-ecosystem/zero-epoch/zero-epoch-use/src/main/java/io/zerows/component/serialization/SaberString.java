package io.zerows.component.serialization;

import io.zerows.platform.constant.VString;

/**
 * String
 */
class SaberString extends SaberBase {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return String.class == paramType ? literal : VString.EMPTY;
    }
}
