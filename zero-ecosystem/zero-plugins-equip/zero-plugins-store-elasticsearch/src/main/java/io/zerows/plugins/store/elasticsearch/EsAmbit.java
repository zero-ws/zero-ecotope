package io.zerows.plugins.store.elasticsearch;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.typed.ChangeFlag;

import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface EsAmbit {

    static EsAmbit create(final ChangeFlag flag, final String index, final JsonObject options) {
        final ConcurrentMap<String, EsAmbit> pool = Pool.ES_CACHE.get(flag);
        return Cc.pool(pool, index, () -> Pool.ES_FUN.get(flag).apply(index, options));
    }

    JsonObject process(String documentId, JsonObject body);

    Boolean process(JsonArray documents, String idField);
}
