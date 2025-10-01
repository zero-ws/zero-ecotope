package io.zerows.extension.runtime.workflow.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.common.shared.datamation.KDictSource;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.workflow.domain.tables.daos.WTicketDao;
import io.zerows.module.cloud.zdk.spi.DictionaryPlugin;
import io.zerows.unity.Ux;

/*
 * Dict for `resource.docs` here
 */
public class TicketDict implements DictionaryPlugin {

    @Override
    public Future<JsonArray> fetchAsync(final KDictSource source,
                                        final MultiMap paramMap) {
        final String sigma = paramMap.get(KName.SIGMA);
        if (Ut.isNotNil(sigma)) {
            return Ux.Jooq.on(WTicketDao.class)
                .fetchAsync(KName.SIGMA, sigma)
                .compose(Ux::futureA);
        } else {
            return Ux.future(new JsonArray());
        }
    }
}
