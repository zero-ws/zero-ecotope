package io.zerows.epoch.corpus;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.program.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("all")
class ToCommon {

    static JsonObject subset(final JsonObject input, final Set<String> removed) {
        removed.forEach(input::remove);
        return input;
    }

    static JsonArray subset(final JsonArray input, final Set<String> removed) {
        Ut.itJArray(input).forEach(json -> subset(json, removed));
        return input;
    }

    static <T> Future<T> future(final T entity) {
        if (entity instanceof Throwable) {
            return Future.failedFuture((Throwable) entity);
        } else {
            return Future.succeededFuture(entity);
        }
    }

    static <T> List<JsonObject> toJList(
        final List<T> list,
        final String pojo
    ) {
        final List<JsonObject> jlist = new ArrayList<>();
        Ut.itJArray(Ut.toJson(list, pojo)).forEach(jlist::add);
        return jlist;
    }

    static JsonObject toToggle(final Object... args) {
        final JsonObject params = new JsonObject();
        for (int idx = 0; idx < args.length; idx++) {
            final String idxStr = String.valueOf(idx);
            params.put(idxStr, args[idx]);
        }
        return params;
    }

    static <T, R> JsonObject toMerge(final T input, final String field, final List<R> list) {
        if (Objects.isNull(input)) {
            return new JsonObject();
        } else {
            final JsonObject serialized = Ut.serializeJson(input);
            if (Objects.nonNull(list)) {
                final JsonArray listData = Ut.serializeJson(list);
                serialized.put(field, listData);
            }
            return serialized;
        }
    }
}
