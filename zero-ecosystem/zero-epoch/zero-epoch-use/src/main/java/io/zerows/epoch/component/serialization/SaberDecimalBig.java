package io.zerows.epoch.component.serialization;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * BigDecimal
 */
@SuppressWarnings("unchecked")
class SaberDecimalBig extends SaberDecimalBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return BigDecimal.class == paramType;
    }

    @Override
    protected Function<String, BigDecimal> getFun() {
        return BigDecimal::new;
    }

    @Override
    public <T> Object from(final T input) {
        final BigDecimal decimal = (BigDecimal) input;
        return decimal.doubleValue();
    }
}
