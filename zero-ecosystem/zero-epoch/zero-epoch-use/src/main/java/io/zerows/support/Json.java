package io.zerows.support;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.metadata.mapping.Mirror;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2024-04-19
 */
class Json {
    static <T> JsonObject toJObject(
        final T entity,
        final String pojo) {
        if (Objects.isNull(entity)) {
            return new JsonObject();
        }
        if (Ut.isNil(pojo)) {
            return Ut.serializeJson(entity, true);
        } else {
            return Mirror.create(Json.class)
                .mount(pojo)
                .connect(Ut.serializeJson(entity, true))
                .to()
                .result();
        }
    }

    static <T> JsonArray toJArray(
        final List<T> list,
        final String pojo
    ) {
        if (Objects.isNull(list)) {
            return new JsonArray();
        }
        final JsonArray array = new JsonArray();
        list.stream()
            .filter(Objects::nonNull)
            .map(item -> toJObject(item, pojo))
            .forEach(array::add);
        return array;
    }

    static <T> T from(final JsonObject data, final Class<T> clazz,
                      final String pojo) {
        if (Ut.isNil(pojo)) {
            return Ut.deserialize(data, clazz, true);
        }
        return Mirror.create(Json.class)
            .mount(pojo)
            .connect(data)
            .type(clazz)
            .from()
            .get();
    }

    @SuppressWarnings("all")
    static <T> List<T> from(final JsonArray data, final Class<?> clazz, final String pojo) {
        final List<T> result = new ArrayList<>();
        Ut.itJArray(data).map(each -> from(each, clazz, pojo))
            .filter(Objects::nonNull)
            .map(item -> (T) item)
            .forEach(result::add);
        return result;
    }
}
