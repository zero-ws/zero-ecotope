package io.zerows.component.serialization;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.program.Ut;

/**
 * Collection
 */
class SaberCollection extends SaberBase {
    @Override
    public <T> Object from(final T input) {
        final String literal = Ut.serialize(input);
        return new JsonArray(literal);
    }

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        // Default direct
        return Ut.deserialize(literal, paramType);
    }
}
