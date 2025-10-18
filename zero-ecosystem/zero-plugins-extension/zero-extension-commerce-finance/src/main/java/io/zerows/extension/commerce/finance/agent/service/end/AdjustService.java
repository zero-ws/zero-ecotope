package io.zerows.extension.commerce.finance.agent.service.end;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTrans;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lang : 2024-01-31
 */
public class AdjustService implements AdjustStub {
    @Override
    public Future<FTrans> adjustAsync(final FTrans trans, final FSettlement settlement) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.Finance.SETTLEMENT_ID, settlement.getKey());

        final DBJooq jq = DB.on(FSettlementItemDao.class);
        return jq.<FSettlementItem>fetchAsync(condition)
            .compose(items -> jq.updateAsync(this.buildData(trans, items)))
            .compose(nil -> Ux.future(trans));
    }

    @Override
    public Future<FTrans> adjustAsync(final FTrans trans, final JsonArray items) {
        final List<FSettlementItem> itemList = Ux.fromJson(items, FSettlementItem.class);
        return DB.on(FSettlementItemDao.class)
            .updateAsync(this.buildData(trans, itemList))
            .compose(nil -> Ux.future(trans));
    }

    private List<FSettlementItem> buildData(final FTrans trans, final List<FSettlementItem> items) {
        // 批量设置 items 中的 finishedId
        items.forEach(item -> {
            item.setFinishedId(trans.getKey());
            item.setUpdatedAt(LocalDateTime.now());
            item.setUpdatedBy(trans.getCreatedBy());
        });
        return items;
    }
}
