package io.zerows.extension.commerce.erp.osgi.spi.dict;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.common.shared.datamation.KDictSource;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.erp.domain.tables.daos.EBrandDao;
import io.zerows.module.cloud.zdk.spi.DictionaryPlugin;
import io.zerows.unity.Ux;

/*
 * Dict for `resource.brands` here
 */
public class BrandDict implements DictionaryPlugin {

    @Override
    public Future<JsonArray> fetchAsync(final KDictSource source,
                                        final MultiMap paramMap) {
        final String sigma = paramMap.get(KName.SIGMA);
        if (Ut.isNotNil(sigma)) {
            return Ux.Jooq.on(EBrandDao.class)
                .fetchAsync(KName.SIGMA, sigma)
                .compose(Ux::futureA);
        } else {
            return Ux.future(new JsonArray());
        }
    }

    @Override
    public JsonArray fetch(final KDictSource source,
                           final MultiMap paramMap) {
        final String sigma = paramMap.get(KName.SIGMA);
        if (Ut.isNotNil(sigma)) {
            return Ux.Jooq.on(EBrandDao.class)
                .fetchJ(KName.SIGMA, sigma);
        } else {
            return new JsonArray();
        }
    }
}
