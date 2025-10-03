package io.zerows.extension.commerce.finance.uca.enter;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.runtime.skeleton.refine.Ke;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2024-01-18
 */
class UpdaterSettlementItem implements Maker<FSettlement, FSettlementItem> {
    /**
     * 「生成结算项」
     * 根据结算单以及各个账单子项生成结算子项，此处是从账单跨越到结算单的过程
     * <pre><code>
     *     1. 移除 `key` 主键值（生成结算单的时候会自动处理）
     *     2. 将结算单关联到当前 settlement 单据，此处有两种情况
     *        - settlementId，此值没有任何争议
     *        - relatedId，此值可以是订单ID，也可以是批次ID
     *     3. 假设结算单号为 CODE-XX，那么结算单子项编号为：
     *        - CODE-XX-001
     *        - CODE-XX-002
     *        ......
     *        - CODE-XX-00N
     * </code></pre>
     *
     * @param dataA      账单子项数据
     * @param settlement 结算单
     **/
    @Override
    public Future<List<FSettlementItem>> buildAsync(final JsonArray dataA, final FSettlement settlement) {
        final List<FBillItem> items = Ux.fromJson(dataA, FBillItem.class);
        final List<FSettlementItem> settlements = new ArrayList<>();
        for (int idx = 0; idx < items.size(); idx++) {
            final FBillItem item = items.get(idx);
            final JsonObject record = Ux.toJson(item);

            record.remove(KName.KEY);
            final FSettlementItem settlementItem = Ux.fromJson(record, FSettlementItem.class);
            settlementItem.setSettlementId(settlement.getKey());
            settlementItem.setRelatedId(item.getKey());
            settlementItem.setIncome(item.getIncome());

            Ke.umCreated(settlementItem, item);
            settlementItem.setSerial(settlement.getSerial() + "-" + Ut.fromAdjust(idx + 1, 3));
            settlementItem.setCode(settlement.getCode() + "-" + Ut.fromAdjust(idx + 1, 3));
            settlements.add(settlementItem);
        }
        return Ux.future(settlements);
    }
}
