package io.zerows.module.domain.uca.serialization;

import io.zerows.ams.constant.VString;

/**
 * StringBuffer, StringBuilder
 */
class StringBufferSaber extends AbstractSaber {
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
