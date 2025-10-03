package io.zerows.weaver;

import io.zerows.support.Ut;

class SaberCommon extends SaberBase {
    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (!SaberTypes.isSupport(paramType)) {
            return Ut.deserialize(literal, paramType, true);
        }
        return null;
    }

    @Override
    public <T> Object from(final T input) {
        Object reference = null;
        if (!SaberTypes.isSupport(input.getClass())) {
            // final String literal = Ut.serialize(input);
            reference = Ut.serializeJson(input, true); // new JsonObject(literal);
        }
        return reference;
    }
}
