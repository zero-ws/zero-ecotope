package io.zerows.extension.commerce.lbs.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.common.datamation.KDictSource;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LFloorDao;
import io.zerows.module.cloud.zdk.spi.DictionaryPlugin;
import io.zerows.unity.Ux;

/*
 * Dict for `location.floors` here
 */
public class FloorDict implements DictionaryPlugin {

    @Override
    public Future<JsonArray> fetchAsync(final KDictSource source,
                                        final MultiMap paramMap) {
        final String sigma = paramMap.get(KName.SIGMA);
        if (Ut.isNotNil(sigma)) {
            return Ux.Jooq.on(LFloorDao.class)
                .fetchAsync(KName.SIGMA, sigma)
                .compose(Ux::futureA);
        } else {
            return Ux.future(new JsonArray());
        }
    }
}
