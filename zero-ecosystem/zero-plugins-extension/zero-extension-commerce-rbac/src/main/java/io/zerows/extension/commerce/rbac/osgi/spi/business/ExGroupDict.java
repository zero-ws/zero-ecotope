package io.zerows.extension.commerce.rbac.osgi.spi.business;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.spi.DictionaryPlugin;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SGroupDao;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/*
 * Dict for `security.groups` here
 */
public class ExGroupDict implements DictionaryPlugin {

    @Override
    public Future<JsonArray> fetchAsync(final KDictConfig.Source source,
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
