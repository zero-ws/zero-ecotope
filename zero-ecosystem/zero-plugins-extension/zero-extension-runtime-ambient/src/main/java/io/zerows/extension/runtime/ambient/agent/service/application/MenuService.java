package io.zerows.extension.runtime.ambient.agent.service.application;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.fn.Fx;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XMenuDao;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MenuService implements MenuStub {

    @Override
    public Future<JsonArray> fetchByApp(final String appId) {
        return Ux.Jooq.on(XMenuDao.class)
            .fetchJAsync(KName.APP_ID, appId)
            // metadata field extraction
            .compose(Fx.ofJArray(KName.METADATA));
    }
}
