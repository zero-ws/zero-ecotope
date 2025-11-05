package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XModuleDao;
import io.zerows.extension.module.ambient.servicespec.ModelStub;
import io.zerows.extension.skeleton.spi.ScModeling;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class ModelService implements ModelStub {
    /*
     * Module cache for each application
     * Normalize standard module here with `Ex.yiStandard` method to fast module configuration fetching
     */
    private static final ConcurrentMap<String, JsonObject> CACHE_MODULE = new ConcurrentHashMap<>();

    @Override
    public Future<JsonObject> fetchModule(final String appId, final String entry) {
        final JsonObject filters = new JsonObject()
            .put("", Boolean.TRUE)
            .put("entry", entry)
            .put("id", appId);
        /* Cache Module for future usage */
        return this.fetchModule(filters, () -> DB.on(XModuleDao.class)
            .fetchOneAsync(filters)
            .compose(Ux::futureJ)
            /* KMetadata Field Usage */
            .compose(Fx.ofJObject(KName.METADATA)));
    }

    @Override
    public Future<JsonArray> fetchModels(final String sigma) {
        return Ux.channel(ScModeling.class, JsonArray::new,
            model -> model.fetchAsync(sigma));
    }

    @Override
    public Future<JsonArray> fetchAttrs(final String identifier, final String sigma) {
        return Ux.channel(ScModeling.class, JsonArray::new,
            model -> model.fetchAttrs(identifier, sigma));
    }

    private Future<JsonObject> fetchModule(final JsonObject condition, final Supplier<Future<JsonObject>> executor) {
        final String appId = condition.getString("id");
        final String entry = condition.getString("entry");
        if (Ut.isNil(appId, entry)) {
            return Ux.futureJ();
        } else {
            final String cacheKey = appId + ":" + entry;
            // Cache enabled
            final JsonObject cachedData = CACHE_MODULE.getOrDefault(cacheKey, null);
            if (Objects.isNull(cachedData)) {
                return executor.get().compose(dataData -> {
                    if (Objects.nonNull(dataData)) {
                        CACHE_MODULE.put(cacheKey, dataData);
                    }
                    return Ux.future(dataData);
                });
            } else {
                return Ux.future(cachedData);
            }
        }
    }
}
