package io.zerows.extension.runtime.ambient.uca.dict;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.concurrent.ConcurrentMap;

/**
 * ## `X_CATEGORY` Dict
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class DpmCategory implements Dpm {

    @Override
    public Future<ConcurrentMap<String, JsonArray>> fetchAsync(final KDictConfig.Source source, final MultiMap params) {
        return Rapid.map(KWeb.CACHE.DIRECTORY, KWeb.ARGS.V_DATA_EXPIRED).cached(source.getTypes(),
            types -> DB.on(XCategoryDao.class).fetchAndAsync(DpmTool.condition(params, types))
                .compose(Ux::futureG));
    }

    @Override
    public ConcurrentMap<String, JsonArray> fetch(final KDictConfig.Source source, final MultiMap params) {
        final JsonArray dataArray = DB.on(XCategoryDao.class)
            .fetchJAnd(DpmTool.condition(params, source.getTypes()));
        return Ut.elementGroup(dataArray, KName.TYPE);
    }
}
