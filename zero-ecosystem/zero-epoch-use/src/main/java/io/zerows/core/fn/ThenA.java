package io.zerows.core.fn;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.ams.fn.FnBase;
import io.zerows.core.util.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * @author lang : 2023/4/27
 */
@SuppressWarnings("all")
final class ThenA {
    private ThenA() {
    }

    static Future<JsonArray> combineA(final Future<JsonArray> source,
                                      final Function<JsonObject, Future<JsonObject>> generateOf, final BinaryOperator<JsonObject> combinerOf) {
        return source.compose(first -> {
            // 并行异步
            final List<Future<?>> secondFutures = new ArrayList<>();
            first.stream()
                .filter(item -> item instanceof JsonObject)
                .map(item -> (JsonObject) item)
                .map(generateOf::apply)
                .forEach(secondFutures::add);
            // 组合结果
            return Future.join(secondFutures).compose(finished -> {
                final List<JsonObject> secondary = finished.list();
                // 拉平后执行组合
                final List<JsonObject> completed = Ut.elementZip(first.getList(), secondary, combinerOf);
                return Future.succeededFuture(new JsonArray(completed));
            }).otherwise(FnBase.outAsync(JsonArray::new));
        }).otherwise(FnBase.outAsync(JsonArray::new));
    }

    static Future<JsonArray> combineA(final List<Future<JsonObject>> futures) {
        return Future.join(new ArrayList<>(futures)).compose(finished -> {
            final JsonArray result = Objects.isNull(finished)
                ? new JsonArray() : new JsonArray(finished.list());
            return Future.succeededFuture(result);
        }).otherwise(FnBase.outAsync(JsonArray::new));
    }

    static Future<JsonArray> compressA(final List<Future<JsonArray>> futures) {
        final List<Future<?>> futureList = new ArrayList<>(futures);
        return Future.join(futureList).compose(finished -> {
            final JsonArray resultMap = new JsonArray();
            if (null != finished) {
                Ut.itList(finished.list(), (item, index) -> {
                    if (item instanceof JsonArray) {
                        resultMap.addAll((JsonArray) item);
                    }
                });
            }
            return Future.succeededFuture(resultMap);
        }).otherwise(FnBase.outAsync(JsonArray::new));
    }
}
