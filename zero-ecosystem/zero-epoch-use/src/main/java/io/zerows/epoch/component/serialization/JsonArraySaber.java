package io.zerows.epoch.component.serialization;

import io.vertx.core.json.JsonArray;

import java.util.function.Function;

/**
 * JsonArray
 */
@SuppressWarnings("unchecked")
class JsonArraySaber extends AbstractJsonSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return JsonArray.class == paramType;
    }

    @Override
    protected Function<String, JsonArray> getFun() {
        return JsonArray::new;
    }
}
