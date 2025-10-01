package io.zerows.extension.runtime.ambient.uca.dict;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.common.shared.datamation.KDictSource;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import io.zerows.core.util.Ut;
import io.zerows.core.web.cache.Rapid;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XTabularDao;
import io.zerows.unity.Ux;

import java.util.concurrent.ConcurrentMap;

/**
 * ## `X_TABULAR` Dict
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class DpmTabular implements Dpm {

    @Override
    public Future<ConcurrentMap<String, JsonArray>> fetchAsync(final KDictSource source, final MultiMap params) {
        return Rapid.map(KWeb.CACHE.DIRECTORY, KWeb.ARGS.V_DATA_EXPIRED).cached(source.getTypes(),
            types -> Ux.Jooq.on(XTabularDao.class).fetchAndAsync(DpmTool.condition(params, types))
                .compose(Ux::futureG));
    }

    @Override
    public ConcurrentMap<String, JsonArray> fetch(final KDictSource source, final MultiMap params) {
        final JsonArray dataArray = Ux.Jooq.on(XTabularDao.class)
            .fetchJAnd(DpmTool.condition(params, source.getTypes()));
        return Ut.elementGroup(dataArray, KName.TYPE);
    }
}
