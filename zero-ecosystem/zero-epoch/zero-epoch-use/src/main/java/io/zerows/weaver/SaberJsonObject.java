package io.zerows.weaver;

import io.vertx.core.json.JsonObject;

import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * JsonObject
 */
@SuppressWarnings("unchecked")
class SaberJsonObject extends SaberJsonBase {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return JsonObject.class == paramType || LinkedHashMap.class == paramType;
    }

    @Override
    protected Function<String, JsonObject> getFun() {
        return JsonObject::new;
    }
}
