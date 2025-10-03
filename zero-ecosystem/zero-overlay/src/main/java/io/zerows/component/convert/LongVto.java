package io.zerows.component.convert;

import io.zerows.platform.constant.VValue;
import io.zerows.support.UtBase;

import java.math.BigDecimal;
import java.util.Objects;

public class LongVto implements Vto<Long> {

    @Override
    @SuppressWarnings("all")
    public Long to(final Object value, final Class<?> type) {
        if (Objects.isNull(value)) {
            /*
             * -1 as default
             */
            return (long) VValue.RANGE;
        } else {
            if (Long.class == type) {
                /*
                 * Default
                 */
                return (Long) value;
            } else if (String.class == type) {
                /*
                 * String -> Long
                 */
                return UtBase.isInteger(value.toString()) ?
                    Long.parseLong(value.toString()) :
                    VValue.RANGE;
            } else if (UtBase.isInteger(type)) {
                /*
                 * Integer -> Long
                 * Short -> Long
                 */
                return Long.parseLong(value.toString());
            } else if (UtBase.isDecimal(type)) {
                /*
                 * Double -> Long
                 * Float -> Long
                 */
                final Double normalized = Double.parseDouble(value.toString());
                return normalized.longValue();
            } else if (BigDecimal.class == type) {

                return ((BigDecimal) value).longValue();
            } else if (Boolean.class == type) {
                /*
                 * Boolean -> Long
                 */
                final Boolean normalized = (Boolean) value;
                if (normalized) {
                    return (long) VValue.ONE;
                } else {
                    return (long) VValue.ZERO;
                }
            }
        }
        return null;
    }
}
