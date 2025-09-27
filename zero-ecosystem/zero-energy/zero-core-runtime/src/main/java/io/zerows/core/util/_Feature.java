package io.zerows.core.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2024-04-19
 */
class _Feature extends _Element {
    protected _Feature() {
    }

    @SuppressWarnings("all")
    public static <T> Future<T> future(final T entity) {
        if (entity instanceof Throwable) {
            return Future.failedFuture((Throwable) entity);
        } else {
            return Future.succeededFuture(entity);
        }
    }

    public static <T> Future<T> future() {
        return Future.succeededFuture();
    }

    public static Future<JsonArray> futureA() {
        return Future.succeededFuture(new JsonArray());
    }
    
    public static <T> Future<JsonArray> futureA(final List<T> list) {
        return Future.succeededFuture(Json.toJArray(list, ""));
    }

    public static Future<JsonObject> futureJ() {
        return Future.succeededFuture(new JsonObject());
    }

    public static Future<Boolean> futureT() {
        return Future.succeededFuture(Boolean.TRUE);
    }

    public static Future<Boolean> futureF() {
        return Future.succeededFuture(Boolean.FALSE);
    }

    public static <T> Future<List<T>> futureL() {
        return Future.succeededFuture(new ArrayList<>());
    }

    @SuppressWarnings("all")
    public static <T> Function<Throwable, T> otherwise(final Supplier<T> supplier) {
        return error -> {
            if (Objects.nonNull(error)) {
                error.printStackTrace();
            }
            return supplier.get();
        };
    }

    public static <T> Function<Throwable, T> otherwise(final T input) {
        return otherwise(() -> input);
    }
}
