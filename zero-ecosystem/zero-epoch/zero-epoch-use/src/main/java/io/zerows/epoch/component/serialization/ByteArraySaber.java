package io.zerows.epoch.component.serialization;

import io.zerows.epoch.constant.VValue;

class ByteArraySaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (Byte[].class == paramType ||
            byte[].class == paramType) {
            return literal.getBytes(VValue.DFT.CHARSET);
        }
        return new byte[0];
    }
}
