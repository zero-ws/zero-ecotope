package io.zerows.component.serialization;

import io.zerows.platform.constant.VString;

/**
 * StringBuffer, StringBuilder
 */
class SaberStringBuffer extends SaberBase {
    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (StringBuilder.class == paramType
            || StringBuffer.class == paramType) {
            if (StringBuffer.class == paramType) {
                return new StringBuffer(literal);
            } else {
                return new StringBuilder(literal);
            }
        }
        return VString.EMPTY;
    }

    @Override
    public <T> Object from(final T input) {
        Object reference = null;
        if (input instanceof StringBuilder
            || input instanceof StringBuffer) {
            reference = input.toString();
        }
        return reference;
    }
}
