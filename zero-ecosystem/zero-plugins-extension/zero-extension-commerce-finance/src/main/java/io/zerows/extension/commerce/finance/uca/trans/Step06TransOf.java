package io.zerows.extension.commerce.finance.uca.trans;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.finance.domain.tables.daos.FTransOfDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTrans;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTransOf;
import io.zerows.extension.commerce.finance.uca.enter.Maker;
import io.zerows.unity.Ux;

import java.util.List;

/**
 * @author lang : 2024-01-25
 */
class Step06TransOf implements Trade<FTrans, FTransOf> {
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
            .compose(Ux.Jooq.on(FTransOfDao.class)::insertAsync);
    }
}
