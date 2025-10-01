package io.zerows.epoch.common.uca.convert;

import io.zerows.ams.util.UtBase;

import java.math.BigDecimal;
import java.util.Objects;

public class FloatVto implements Vto<Float> {

    @Override
    public Float to(final Object value, final Class<?> type) {
        if (Objects.isNull(value)) {

            return -1.0f;
        } else {
            if (Float.class == type) {

                return (Float) value;
            } else if (String.class == type) {

                return UtBase.isDecimal(value.toString()) ? Float.parseFloat(value.toString()) : -1.0f;
            } else if (UtBase.isInteger(type) || UtBase.isDecimal(type)) {

                return Float.parseFloat(value.toString());
            } else if (BigDecimal.class == type) {

                return ((BigDecimal) value).floatValue();
            } else if (Boolean.class == type) {

                final Boolean normalized = (Boolean) value;
                if (normalized) {
                    return 1.0f;
                } else {
                    return 0.0f;
                }
            }
        }
        return null;
    }
}
