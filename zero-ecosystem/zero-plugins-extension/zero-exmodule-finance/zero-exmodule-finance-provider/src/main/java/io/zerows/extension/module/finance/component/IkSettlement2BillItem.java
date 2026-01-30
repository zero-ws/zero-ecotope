package io.zerows.extension.module.finance.component;

import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 结算核心步骤
 * <pre><code>
 *     1. {@link FSettlement} -> {@link List<FBillItem>}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class IkSettlement2BillItem implements IkWay<FSettlement, FBillItem> {
    /**
     * 「结算」现结模式的结算处理
     * <pre><code>
     *     账单子项中包含了两个特殊字段：
     *     - billId：关联账单信息
     *     - settlementId：关联结算单项
     *     当某个子项被结算后，需要将子项中的结算单项关联信息填充，而且此时不更改
     *     子项的状态不发生任何改变。
     * </code></pre>
     *
     * @param settlement from = 结算单
     * @param items      to = 账单子项
     */
    @Override
    public void transfer(final FSettlement settlement, final List<FBillItem> items) {
        items.forEach(item -> {
            item.setSettlementId(settlement.getId());
            item.setUpdatedAt(LocalDateTime.now());
            item.setUpdatedBy(settlement.getUpdatedBy());
        });
    }
}
