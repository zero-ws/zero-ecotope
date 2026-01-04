package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * ## `X_CATEGORY` Dict
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class DpmCategory implements Dpm {

    @Override
    public Future<ConcurrentMap<String, JsonArray>> fetchAsync(final KDictConfig.Source source, final MultiMap params) {
        return HMM.<Set<String>, ConcurrentMap<String, JsonArray>>of(KWeb.CACHE.DIRECTORY).cached(
            source.getTypes(),
            () -> DB.on(XCategoryDao.class)
                .fetchAndAsync(DpmTool.condition(params, source.getTypes()))
                .compose(Ux::futureG),
            KWeb.ARGS.V_DATA_EXPIRED
        );
    }

    @Override
    public ConcurrentMap<String, JsonArray> fetch(final KDictConfig.Source source, final MultiMap params) {
        final JsonArray dataArray = DB.on(XCategoryDao.class)
            .fetchJAnd(DpmTool.condition(params, source.getTypes()));
        return Ut.elementGroup(dataArray, KName.TYPE);
    }
}
