package io.zerows.epoch.bootplus.extension.uca.elasticsearch;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.typed.ChangeFlag;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/*
 *
 */
public interface EsIndex {

    static EsIndex create(final ChangeFlag type, final String identifier) {
        final Function<String, EsIndex> executor = PoolInternal.POOL_INDEX_SUPPLIER.get(type);
        final ConcurrentMap<String, EsIndex> pool = PoolInternal.POOL_INDEX.get(type);
        if (Objects.isNull(pool)) {
            return executor.apply(identifier);
        } else {
            return Cc.pool(pool, identifier, () -> executor.apply(identifier));
        }
    }


    Future<JsonObject> indexAsync(JsonObject record);

    Future<JsonArray> indexAsync(JsonArray record);
}

