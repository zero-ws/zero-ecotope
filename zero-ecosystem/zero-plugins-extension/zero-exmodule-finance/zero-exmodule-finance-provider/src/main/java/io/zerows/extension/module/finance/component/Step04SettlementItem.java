package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.domain.tables.daos.FSettlementItemDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 步骤四：根据数据生成最终的 {@link FSettlementItem} 的列表
 *
 * @author lang : 2024-01-22
 */
class Step04SettlementItem implements Step<FSettlement, FSettlementItem> {
    @Override
    public Future<List<FSettlementItem>> scatter(final JsonArray data, final FSettlement inserted) {
        /*
         * STI 构造来处理账单明细信息
         * 直接用账单明细 FBillItem 中的数据来构造 FSettlementItem，并且将 FSettlementItem 插入到数据库中
         * 保存起来。
         */
        return Maker.upSTI().buildAsync(data, inserted)
            .compose(DB.on(FSettlementItemDao.class)::insertAsync);
    }

    @Override
    public Future<List<FSettlementItem>> flatter(final JsonArray data, final List<FSettlement> settlements) {
        final List<Future<List<FSettlementItem>>> futures = new ArrayList<>();
        final ConcurrentMap<String, JsonArray> grouped = Ut.elementGroup(data, KName.Finance.SETTLEMENT_ID);
        final ConcurrentMap<String, FSettlement> settlementMap = Ut.elementMap(settlements, FSettlement::getId);
        grouped.forEach((settlementId, items) -> {
            if (Ut.isNotNil(settlementId) && Ut.isNotNil(items)) {
                final FSettlement settlement = settlementMap.get(settlementId);
                futures.add(Maker.upSTI().buildAsync(items, settlement));
            }
        });
        return Fx.combineT(futures)
            .compose(result -> {
                final List<FSettlementItem> inserted = new ArrayList<>();
                result.forEach(inserted::addAll);
                return Ux.future(inserted);
            }).compose(DB.on(FSettlementItemDao.class)::insertAsync);
    }
}
