package io.zerows.extension.commerce.erp.osgi.spi.dict;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.spi.DictionaryPlugin;
import io.zerows.extension.commerce.erp.domain.tables.daos.EBrandDao;
import io.zerows.platform.metadata.KDictSource;
import io.zerows.support.Ut;

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
