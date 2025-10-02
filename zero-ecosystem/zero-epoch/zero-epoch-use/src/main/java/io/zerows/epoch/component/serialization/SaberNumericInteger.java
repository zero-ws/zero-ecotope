package io.zerows.epoch.component.serialization;

import java.util.function.Function;

/**
 * Integer type
 */
@SuppressWarnings("unchecked")
class SaberNumericInteger extends SaberNumericBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return int.class == paramType || Integer.class == paramType;
    }

    @Override
    protected Function<String, Integer> getFun() {
        return Integer::parseInt;
    }
}
