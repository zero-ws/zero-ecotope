package io.zerows.weaver;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;

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
        return literal -> {
            try {
                return new JsonArray(literal);
            } catch (final DecodeException ex) {
                /*
                 * Compatibility:
                 * Some clients send JsonObject wrapper: { "data": [ ... ] }.
                 * Keep old behaviour for real JsonArray literals, but unwrap when possible.
                 */
                try {
                    final JsonObject wrapper = new JsonObject(literal);
                    final JsonArray data = wrapper.getJsonArray(KName.DATA);
                    if (data != null) {
                        return data;
                    }
                    final JsonArray dataAlt = wrapper.getJsonArray("data");
                    if (dataAlt != null) {
                        return dataAlt;
                    }
                } catch (final DecodeException ignored) {
                    // ignored
                }
                throw ex;
            }
        };
    }
}
