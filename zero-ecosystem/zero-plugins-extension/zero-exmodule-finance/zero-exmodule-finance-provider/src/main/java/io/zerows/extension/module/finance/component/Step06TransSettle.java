package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.common.em.EmTran;
import io.zerows.extension.module.finance.domain.tables.daos.FTransDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.module.finance.domain.tables.pojos.FTrans;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2024-01-24
 */
class Step06TransSettle implements Step<List<FSettlement>, FTrans> {

    @Override
    public Future<FTrans> flatter(final JsonObject data, final List<FSettlement> settlements) {
        // 构造 FTrans
        return Maker.ofT().buildFastAsync(data)
            .compose(trans -> {
                /*
                 * 金额计算
                 */
                final List<String> nameList = new ArrayList<>();
                for (final FSettlement settlement : settlements) {
                    if (Objects.nonNull(settlement.getAmount())) {
                        nameList.add(settlement.getCode());
                    }
                }
                trans.setType(EmTran.Type.SETTLEMENT.name());
                trans.setName("ST:" + Ut.fromJoin(nameList));
                trans.setId(UUID.randomUUID().toString());
                // 此处构造完成
                return Ux.future(trans);
            })
            .compose(DB.on(FTransDao.class)::insertAsync);
    }
}
