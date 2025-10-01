package io.zerows.epoch.corpus.domain.uca.serialization;

import io.zerows.epoch.program.Ut;

class CommonSaber extends AbstractSaber {
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
