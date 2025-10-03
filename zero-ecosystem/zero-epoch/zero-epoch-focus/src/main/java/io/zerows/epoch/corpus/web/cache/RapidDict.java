package io.zerows.epoch.corpus.web.cache;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.support.fn.Fx;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class RapidDict extends AbstractRapid<Set<String>, ConcurrentMap<String, JsonArray>> {
    RapidDict(final String cacheKey, final int expired) {
        super(cacheKey, expired);
    }

    @Override
    public Future<ConcurrentMap<String, JsonArray>> cached(
        final Set<String> keys,
        final Function<Set<String>, Future<ConcurrentMap<String, JsonArray>>> executor) {
        Objects.requireNonNull(keys);
        return this.pool.<String, JsonArray>get(keys).compose(dataMap -> {
            final Set<String> keySet = Ut.elementDiff(keys, dataMap.keySet());
            final ConcurrentMap<String, JsonArray> cached = new ConcurrentHashMap<>(dataMap);
            if (keySet.isEmpty()) {
                return Ut.future(cached);
            } else {
                return executor.apply(keySet).compose(queried -> {
                    final ConcurrentMap<String, Future<JsonArray>> futureMap = new ConcurrentHashMap<>();
                    queried.forEach((key, data) ->
                        futureMap.put(key, this.pool.put(key, data, this.expired)
                            .compose(kv -> Ut.future(kv.value()))));
                    return Fx.combineM(futureMap);
                }).compose(newMap -> {
                    cached.putAll(newMap);
                    return Ut.future(cached);
                });
            }
        });
    }
}
