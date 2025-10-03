package io.zerows.extension.commerce.erp.osgi.spi.dict;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.based.constant.KName;
import io.zerows.component.shared.datamation.KDictSource;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.underlying.DictionaryPlugin;
import io.zerows.extension.commerce.erp.domain.tables.daos.EBrandDao;

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
