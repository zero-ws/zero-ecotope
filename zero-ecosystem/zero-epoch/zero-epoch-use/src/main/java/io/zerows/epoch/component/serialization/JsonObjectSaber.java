package io.zerows.epoch.component.serialization;

import io.vertx.core.json.JsonObject;

import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * JsonObject
 */
@SuppressWarnings("unchecked")
class JsonObjectSaber extends AbstractJsonSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return JsonObject.class == paramType || LinkedHashMap.class == paramType;
    }

    @Override
    protected Function<String, JsonObject> getFun() {
        return JsonObject::new;
    }
}
