package io.zerows.extension.commerce.psi.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.unity.Ux;
import io.zerows.common.datamation.KDictSource;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.psi.domain.tables.daos.PWhDao;
import io.zerows.module.cloud.zdk.spi.DictionaryPlugin;

public class WhDict implements DictionaryPlugin {
    @Override
    public Future<JsonArray> fetchAsync(final KDictSource source, final MultiMap paramMap) {
        final String sigma = paramMap.get(KName.SIGMA);
        if (Ut.isNotNil(sigma)) {
            return Ux.Jooq.on(PWhDao.class)
                .fetchAsync(KName.SIGMA, sigma)
                .compose(Ux::futureA);
        } else {
            return Ux.future(new JsonArray());
        }
    }
}
