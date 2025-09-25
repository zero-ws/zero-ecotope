package io.zerows.core.web.model.commune;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VName;
import io.zerows.core.exception.WebException;
import io.zerows.module.domain.uca.serialization.ZeroType;

import java.util.Objects;

class RibTool {

    static <T> JsonObject input(final T data) {
        final Object serialized = ZeroType.valueSupport(data);
        final JsonObject bodyData = new JsonObject();
        bodyData.put(VName.DATA, serialized);
        return bodyData;
    }

    @SuppressWarnings("all")
    static <T> T deserialize(final Object value, final Class<?> clazz) {
        T reference = null;
        if (Objects.nonNull(value)) {
            final Object result = ZeroType.value(clazz, value.toString());
            reference = (T) result;
        }
        return reference;
    }

    static JsonObject outJson(final JsonObject data, final WebException error) {
        if (Objects.isNull(error)) {
            return data;
        } else {
            return error.toJson();
        }
    }

    static Buffer outBuffer(final JsonObject data, final WebException error) {
        if (Objects.isNull(error)) {
            // final JsonObject response = data.getJsonObject(VName.DATA);
            return data.getBuffer(VName.DATA);
        } else {
            final JsonObject errorJson = error.toJson();
            return Buffer.buffer(errorJson.encode());
        }
    }
}
