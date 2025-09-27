package io.zerows.module.domain.uca.serialization;

import java.util.function.Function;

/**
 * Integer type
 */
@SuppressWarnings("unchecked")
class IntegerSaber extends AbstractNumericSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return int.class == paramType || Integer.class == paramType;
    }

    @Override
    protected Function<String, Integer> getFun() {
        return Integer::parseInt;
    }
}
