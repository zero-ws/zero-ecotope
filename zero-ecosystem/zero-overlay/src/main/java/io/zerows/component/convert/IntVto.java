package io.zerows.component.convert;

import io.zerows.platform.constant.VValue;
import io.zerows.support.UtBase;

import java.math.BigDecimal;
import java.util.Objects;

public class IntVto implements Vto<Integer> {

    @Override
    public Integer to(final Object value, final Class<?> type) {
        if (Objects.isNull(value)) {
            /*
             * -1 as default
             */
            return VValue.RANGE;
        } else {
            if (Integer.class == type) {
                /*
                 * Default
                 */
                return (Integer) value;
            } else if (String.class == type) {
                /*
                 * String -> Integer
                 */
                return UtBase.isInteger(value.toString()) ?
                    Integer.parseInt(value.toString()) :
                    VValue.RANGE;
            } else if (UtBase.isInteger(type)) {
                /*
                 * Long -> Integer
                 * Short -> Integer
                 */
                return Integer.parseInt(value.toString());
            } else if (UtBase.isDecimal(type)) {
                /*
                 * Double -> Integer
                 * Float -> Integer
                 */
                final double normalized = Double.parseDouble(value.toString());
                return (int) normalized;
            } else if (BigDecimal.class == type) {

                return ((BigDecimal) value).intValue();
            } else if (Boolean.class == type) {
                /*
                 * Boolean -> Integer
                 */
                final Boolean normalized = (Boolean) value;
                if (normalized) {
                    return VValue.ONE;
                } else {
                    return VValue.ZERO;
                }
            }
        }
        return null;
    }
}
