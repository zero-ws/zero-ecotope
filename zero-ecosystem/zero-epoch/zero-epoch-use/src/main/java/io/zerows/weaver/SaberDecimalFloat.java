package io.zerows.weaver;

import java.util.function.Function;

/**
 * Float type
 */
@SuppressWarnings("unchecked")
class SaberDecimalFloat extends SaberDecimalBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return float.class == paramType || Float.class == paramType;
    }

    @Override
    protected Function<String, Float> getFun() {
        return Float::parseFloat;
    }
}
