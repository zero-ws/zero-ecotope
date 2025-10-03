package io.zerows.component.serialization;

import io.vertx.core.json.JsonArray;

import java.util.function.Function;

/**
 * JsonArray
 */
@SuppressWarnings("unchecked")
class SaberJsonArray extends SaberJsonBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return JsonArray.class == paramType;
    }

    @Override
    protected Function<String, JsonArray> getFun() {
        return JsonArray::new;
    }
}
