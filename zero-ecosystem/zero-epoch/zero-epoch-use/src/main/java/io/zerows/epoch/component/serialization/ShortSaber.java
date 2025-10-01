package io.zerows.epoch.component.serialization;

import java.util.function.Function;

/**
 * Short type
 */
@SuppressWarnings("unchecked")
class ShortSaber extends AbstractNumericSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return short.class == paramType || Short.class == paramType;
    }

    @Override
    protected Function<String, Short> getFun() {
        return Short::parseShort;
    }
}
