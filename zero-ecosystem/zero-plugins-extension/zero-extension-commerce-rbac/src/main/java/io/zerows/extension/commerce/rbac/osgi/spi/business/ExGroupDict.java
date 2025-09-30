package io.zerows.extension.commerce.rbac.osgi.spi.business;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.common.datamation.KDictSource;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SGroupDao;
import io.zerows.module.cloud.zdk.spi.DictionaryPlugin;
import io.zerows.unity.Ux;

/*
 * Dict for `security.groups` here
 */
public class ExGroupDict implements DictionaryPlugin {

    @Override
    public Future<JsonArray> fetchAsync(final KDictSource source,
                                        final MultiMap paramMap) {
        final String sigma = paramMap.get(KName.SIGMA);
        if (Ut.isNotNil(sigma)) {
            return Ux.Jooq.on(SGroupDao.class)
                .fetchAsync(KName.SIGMA, sigma)
                .compose(Ux::futureA);
        } else {
            return Ux.future(new JsonArray());
        }
    }
}
