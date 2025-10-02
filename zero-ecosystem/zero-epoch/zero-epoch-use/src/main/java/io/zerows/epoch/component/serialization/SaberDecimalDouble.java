package io.zerows.epoch.component.serialization;

import java.util.function.Function;

/**
 * Double type
 */
@SuppressWarnings("unchecked")
class SaberDecimalDouble extends SaberDecimalBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return double.class == paramType || Double.class == paramType;
    }

    @Override
    protected Function<String, Double> getFun() {
        return Double::parseDouble;
    }
}
