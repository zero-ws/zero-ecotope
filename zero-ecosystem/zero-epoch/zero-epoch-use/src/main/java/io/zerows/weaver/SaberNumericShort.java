package io.zerows.weaver;

import java.util.function.Function;

/**
 * Short type
 */
@SuppressWarnings("unchecked")
class SaberNumericShort extends SaberNumericBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return short.class == paramType || Short.class == paramType;
    }

    @Override
    protected Function<String, Short> getFun() {
        return Short::parseShort;
    }
}
