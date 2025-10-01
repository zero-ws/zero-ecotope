package io.zerows.epoch.corpus.domain.uca.serialization;

import java.util.function.Function;

/**
 * Double type
 */
@SuppressWarnings("unchecked")
class DoubleSaber extends AbstractDecimalSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return double.class == paramType || Double.class == paramType;
    }

    @Override
    protected Function<String, Double> getFun() {
        return Double::parseDouble;
    }
}
