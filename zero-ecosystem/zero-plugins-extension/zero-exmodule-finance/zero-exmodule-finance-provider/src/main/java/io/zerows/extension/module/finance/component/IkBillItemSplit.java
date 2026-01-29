package io.zerows.extension.module.finance.component;

import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.skeleton.common.Ke;

import java.util.List;
import java.util.Objects;

/**
 * 拆账专用，一个核心方法
 * <pre><code>
 *     1. {@link FBillItem} -> {@link List<FBillItem>}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class IkBillItemSplit implements IkWay<FBillItem, FBillItem> {
    /**
     * 「拆账」拆账操作，将一个账单拆分成多个账单，基础规则如：
     * <pre><code>
     *     拆之前：
     *     1. status = InValid
     *     2. active = false
     *
     *     拆之后
     *     1. 拆之前的编号为：CODE-XX，那么拆之后为：
     *        - CODE-XXA
     *        - CODE-XXB
     *        ......
     *     2. status = Pending
     *     3. 拆之后的账单需清空 key 主键值（新建时自动生成）
     *     4. active = true
     *     5. relatedId = 原始项（关联）
     * </code></pre>
     * 目前的版本是一分为二，即拆分成两个账单，本身逻辑是可以支持一个分为多个的，取决于
     * 您目前的序号系统，目前支持：{@link FmConstant#SEQ} 中的基础定义
     * A - G 的编号。
     *
     * @param item  from = 拆之前账单项
     * @param items to = 拆之后账单项
     */
    @Override
    public void transfer(final FBillItem item, final List<FBillItem> items) {
        Objects.requireNonNull(item);
        if (Objects.isNull(items) || items.isEmpty()) {
            return;
        }
        final int size = items.size();
        item.setActive(Boolean.FALSE);          // Old Disabled
        item.setStatus(FmConstant.Status.INVALID);
        for (int idx = 0; idx < size; idx++) {
            final FBillItem split = items.get(idx);
            split.setId(null);
            split.setStartAt(item.getStartAt());
            split.setBillId(item.getBillId());
            split.setSerial(item.getSerial() + FmConstant.SEQ[idx]);
            split.setCode(item.getCode() + FmConstant.SEQ[idx]);
            split.setStatus(FmConstant.Status.PENDING);
            split.setRelatedId(item.getId());
            split.setIncome(item.getIncome());
            // active, sigma
            Ke.umCreated(split, item);
            split.setActive(Boolean.TRUE);      // New Enabled
        }
    }
}
