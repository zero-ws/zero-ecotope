package io.zerows.extension.module.finance.common;

import java.math.BigDecimal;

/**
 * @author lang : 2024-01-22
 */
class FmAmount {

    static BigDecimal calcAmount(final BigDecimal start, final BigDecimal adjust,
                                 final boolean income, final String status) {
        final boolean statusInValid = FmConstant.Status.INVALID.equals(status) || FmConstant.Status.FINISHED.equals(status);
        if (income) {
            if (statusInValid) {
                // Move out, Consume, -
                return start.subtract(adjust);
            } else {
                // Move in, Consume, +
                return start.add(adjust);
            }
        } else {
            if (statusInValid) {
                // Move out, Pay, +
                return start.add(adjust);
            } else {
                // Move in, Pay, -
                return start.subtract(adjust);
            }
        }
    }
}
