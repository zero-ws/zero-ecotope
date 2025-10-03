package io.zerows.support.fn;

import io.vertx.core.Future;
import io.zerows.platform.constant.VValue;
import io.zerows.support.base.FnBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BinaryOperator;

/**
 * @author lang : 2023/4/27
 */
final class ThenM {
    private ThenM() {
    }

    @SuppressWarnings("all")
    static <K, T> Future<ConcurrentMap<K, T>> combineM(final ConcurrentMap<K, Future<T>> futureMap) {
        final List<K> keys = new ArrayList<>();
        final List<Future<?>> futures = new ArrayList<>();
        futureMap.forEach((key, future) -> {
            keys.add(key);
            futures.add(future);
        });
        return Future.join(futures).compose(finished -> {
            final List<T> list = finished.list();
            /*
             * Index mapping
             */
            final int size = list.size();
            final ConcurrentMap<K, T> resultMap = new ConcurrentHashMap<>();
            for (int idx = VValue.IDX; idx < size; idx++) {
                final K key = keys.get(idx);
                final T result = list.get(idx);
                if (Objects.nonNull(key) && Objects.nonNull(result)) {
                    resultMap.put(key, result);
                }
            }
            return Future.succeededFuture(resultMap);
        }).otherwise(FnBase.outAsync(ConcurrentHashMap::new));
    }

    /*
     * List<Future<Map<String,Tool>>> futures ->
     *      Future<Map<String,Tool>>
     * Exchange data by key here.
     *      The binary operator should ( Tool, Tool ) -> Tool
     */
    static <T> Future<ConcurrentMap<String, T>> compressM(
        final List<Future<ConcurrentMap<String, T>>> futures,
        final BinaryOperator<T> binaryOperator
    ) {
        /* thenResponse */
        return Future.join(new ArrayList<>(futures)).compose(finished -> {
            final ConcurrentMap<String, T> resultMap = new ConcurrentHashMap<>();
            if (Objects.nonNull(finished)) {
                final List<ConcurrentMap<String, T>> result = finished.list();

                final BinaryOperator<T> mergeOperator = Objects.isNull(binaryOperator) ?
                    /*
                     * Default set merged function to
                     * latest replace original Tool in result map
                     * For other situation, the system should call binaryOperator
                     * to merge (Tool, Tool) -> Tool
                     * 1) JsonArray
                     * 2) List<Tool>
                     * 3) Others
                     *
                     * */
                    (original, latest) -> latest : binaryOperator;
                /*
                 * List<ConcurrentMap<String,Tool>> result ->
                 *      ConcurrentMap<String,Tool>
                 */
                result.stream().filter(Objects::nonNull).forEach(each -> each.keySet()
                    .stream().filter(key -> Objects.nonNull(each.get(key))).forEach(key -> {
                        final T combined;
                        if (resultMap.containsKey(key)) {
                            /*
                             * Merged key -> value to result
                             */
                            final T original = resultMap.get(key);
                            final T latest = each.get(key);
                            combined = mergeOperator.apply(original, latest);
                        } else {
                            /*
                             * Extract combined
                             */
                            combined = each.get(key);
                        }
                        resultMap.put(key, combined);
                    }));
            }
            return Future.succeededFuture(resultMap);
        }).otherwise(FnBase.outAsync(ConcurrentHashMap::new));
    }

}
