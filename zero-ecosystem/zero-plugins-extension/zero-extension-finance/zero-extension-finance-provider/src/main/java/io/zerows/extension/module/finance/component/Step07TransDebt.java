package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.domain.tables.daos.FTransDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.module.finance.domain.tables.pojos.FTrans;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2024-03-12
 */
class Step07TransDebt implements Step<List<FDebt>, FTrans> {

    @Override
    public Future<FTrans> flatter(final JsonObject data, final List<FDebt> debts) {
        // 构造 FTrans
        return Maker.ofT().buildFastAsync(data)
            .compose(trans -> {
                // 名称
                final List<String> nameList = new ArrayList<>();
                for (final FDebt debt : debts) {
                    if (null != debt.getAmount()) {
                        nameList.add(debt.getCode());
                    }
                }
                trans.setKey(null);
                trans.setName("DR: " + Ut.fromJoin(nameList));
                // 此处构造完成
                return Ux.future(trans);
            })
            .compose(DB.on(FTransDao.class)::insertAsync);
    }
}
