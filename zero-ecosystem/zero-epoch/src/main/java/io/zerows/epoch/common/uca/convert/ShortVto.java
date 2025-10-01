package io.zerows.epoch.common.uca.convert;

import io.zerows.epoch.constant.VValue;
import io.zerows.epoch.support.UtBase;

import java.math.BigDecimal;
import java.util.Objects;

@SuppressWarnings("all")
public class ShortVto implements Vto<Short> {

    @Override
    public Short to(final Object value, final Class<?> type) {
        if (Objects.isNull(value)) {
            /*
             * -1 as default
             */
            return (short) VValue.RANGE;
        } else {
            if (Short.class == type) {
                /*
                 * Default
                 */
                return (Short) value;
            } else if (String.class == type) {
                /*
                 * String -> Short
                 */
                return UtBase.isInteger(value.toString()) ?
                    Short.parseShort(value.toString()) :
                    (short) VValue.RANGE;
            } else if (UtBase.isInteger(type)) {
                /*
                 * Integer -> Short
                 * Long -> Short
                 */
                return Short.parseShort(value.toString());
            } else if (UtBase.isDecimal(type)) {
                /*
                 * Double -> Short
                 * Float -> Short
                 */
                final Double normalized = Double.parseDouble(value.toString());
                return normalized.shortValue();
            } else if (BigDecimal.class == type) {

                return ((BigDecimal) value).shortValue();
            } else if (Boolean.class == type) {
                /*
                 * Boolean -> Short
                 */
                final Boolean normalized = (Boolean) value;
                if (normalized) {
                    return (short) VValue.ONE;
                } else {
                    return (short) VValue.ZERO;
                }
            }
        }
        return null;
    }
}
