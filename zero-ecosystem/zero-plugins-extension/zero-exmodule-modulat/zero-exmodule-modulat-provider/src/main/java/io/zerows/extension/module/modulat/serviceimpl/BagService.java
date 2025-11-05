package io.zerows.extension.module.modulat.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.modulat.domain.tables.daos.BBagDao;
import io.zerows.extension.module.modulat.servicespec.BagStub;
import io.zerows.extension.module.modulat.servicespec.BlockStub;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BagService implements BagStub {

    @Inject
    private transient BlockStub blockStub;

    @Override
    public Future<JsonArray> fetchBag(final String appId) {
        final JsonObject condition = Ux.whereAnd(KName.APP_ID, appId);
        final KRef bagRef = new KRef();
        // Bag Fetching
        return this.fetchBag(condition).compose(bagRef::future)
            // Block Fetching
            .compose(nil -> this.blockStub.fetchByApp(appId))
            // Response Build
            .compose(blocks -> this.output(bagRef.get(), blocks));
    }

    private Future<JsonArray> fetchBag(final JsonObject condition) {
        return DB.on(BBagDao.class).fetchJAsync(condition).compose(Fx.ofJArray(
            KName.Flow.UI_STYLE,
            KName.Flow.UI_CONFIG
        ));
    }

    private Future<JsonArray> output(final JsonArray response, final ConcurrentMap<String, JsonArray> blocks) {
        Ut.itJArray(response).forEach(json -> {
            final String bagKey = json.getString(KName.KEY);
            if (blocks.containsKey(bagKey)) {
                json.put(KName.App.BLOCK, blocks.get(bagKey));
            } else {
                json.put(KName.App.BLOCK, new JsonArray());
            }
        });
        return Ux.future(response);
    }

    @Override
    public Future<JsonArray> fetchExtension(final String appId) {
        final JsonObject condition = Ux.whereAnd(KName.APP_ID, appId);
        condition.put(KName.TYPE, "EXTENSION");
        final KRef bagRef = new KRef();
        // Bag Fetching
        return this.fetchBag(condition).compose(bagRef::future)
            // Block Fetching
            .compose(response -> {
                final Set<String> bagIds = Ut.valueSetString(response, KName.KEY);
                return this.blockStub.fetchByBag(bagIds);
            })
            // Response Build
            .compose(blocks -> this.output(bagRef.get(), blocks));
    }

}
