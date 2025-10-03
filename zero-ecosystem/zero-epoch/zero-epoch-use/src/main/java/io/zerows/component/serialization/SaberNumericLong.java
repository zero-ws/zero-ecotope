package io.zerows.component.serialization;

import java.util.function.Function;

/**
 * Long type
 */
@SuppressWarnings("unchecked")
class SaberNumericLong extends SaberNumericBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return long.class == paramType || Long.class == paramType;
    }

    @Override
    protected Function<String, Long> getFun() {
        return Long::parseLong;
    }
}
