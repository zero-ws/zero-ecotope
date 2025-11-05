package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.domain.tables.daos.FTransOfDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FTrans;
import io.zerows.extension.module.finance.domain.tables.pojos.FTransOf;

import java.util.List;

/**
 * @author lang : 2024-01-25
 */
class Step06TransOf implements Step<FTrans, FTransOf> {
    @Override
    public Future<List<FTransOf>> scatter(final JsonObject data, final FTrans trans) {
        /*
         * {
         *     "type": "xxx",
         *     "comment": "xxx",
         *     "keys": []
         * }
         */
        return Maker.ofTO().buildAsync(data, trans)
            .compose(DB.on(FTransOfDao.class)::insertAsync);
    }
}
