package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.program.Ux;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lang : 2024-01-18
 */
class MakerBillItem implements Maker<FSettlement, FBillItem> {
    /**
     * 「账单结算」
     * 结算过程中的账单子项的处理流程，此处只设置更新数据环节
     * <pre><code>
     *     1. updatedAt / updatedBy
     *     2. status = Finished
     * </code></pre>
     *
     * @param items      账单子项列表数据
     * @param settlement 输入数据
     */
    @Override
    public Future<List<FBillItem>> buildAsync(final JsonArray items, final FSettlement settlement) {
        final List<FBillItem> itemList = Ux.fromJson(items, FBillItem.class);
        itemList.forEach(item -> {
            item.setStatus(FmConstant.Status.FINISHED);
            item.setUpdatedAt(LocalDateTime.now());
            // 同样方式处理
            item.setUpdatedBy(settlement.getUpdatedBy());
        });
        return Ux.future(itemList);
    }
}
