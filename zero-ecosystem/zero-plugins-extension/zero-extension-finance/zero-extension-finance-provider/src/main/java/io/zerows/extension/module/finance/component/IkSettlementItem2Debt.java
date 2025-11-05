package io.zerows.extension.module.finance.component;

import io.zerows.extension.module.finance.common.Fm;
import io.zerows.extension.module.finance.common.em.EmDebt;
import io.zerows.extension.module.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlementItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2024-01-22
 */
class IkSettlementItem2Debt implements IkWay<List<FSettlementItem>, FDebt> {
    @Override
    public void transfer(final List<FSettlementItem> items, final FDebt debt) {
        if (Objects.isNull(items) || items.isEmpty()) {
            return;
        }
        // PKG-FM-102
        //  debt.setSettlementId(settlement.getKey());
        final FSettlementItem first = items.iterator().next();
        debt.setFinished(Boolean.FALSE);            // 此处必须有，防止查询不了数据
        if (Objects.isNull(debt.getCreatedBy())) {
            debt.setCreatedBy(first.getUpdatedBy());
            debt.setCreatedAt(LocalDateTime.now());
        }

        // Amount, 使用遍历
        BigDecimal decimal = new BigDecimal(0);
        for (final FSettlementItem item : items) {
            final BigDecimal amount = item.getAmount();
            decimal = Fm.calcAmount(decimal, amount, item.getIncome());
        }

        debt.setAmount(decimal);
        debt.setAmountBalance(decimal);             // 创建应收时：剩余金额 = 总金额

        if (decimal.doubleValue() < 0) {
            debt.setType(EmDebt.Type.REFUND.name());
        } else {
            debt.setType(EmDebt.Type.DEBT.name());
        }
    }
}
