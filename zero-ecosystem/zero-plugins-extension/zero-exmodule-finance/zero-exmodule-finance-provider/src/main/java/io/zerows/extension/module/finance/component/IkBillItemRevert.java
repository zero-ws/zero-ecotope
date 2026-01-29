package io.zerows.extension.module.finance.component;

import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.skeleton.common.Ke;

import java.util.Objects;

/**
 * 冲账专用，一个核心方法
 * <pre><code>
 *     1. {@link FBillItem} -> {@link FBillItem}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class IkBillItemRevert implements IkWay<FBillItem, FBillItem> {
    /**
     * 「冲账」冲账操作，将一个账单冲掉（目前是对冲模式）：
     * <pre><code>
     *     冲账账单：
     *     1. status = InValid
     *     2. active = false
     *
     *     对冲账单：
     *     1. 对冲账单的编号规则：CODE-XX，那么对应对冲编号：
     *        CODE-XXR
     *     2. status = InValid
     *     3. 冲账之后的账单项需要清空 key 主键值（新建时自动生成）
     *     4. active = true
     *     5. relatedId = 原始项（关联）
     * </code></pre>
     * 注意一点：冲账过程中冲账账单和对冲账单同时将状态设置成 InValid，避免计算
     *
     * @param item from = 账单项
     * @param to   to = 对冲账单项
     */
    @Override
    public void transfer(final FBillItem item, final FBillItem to) {
        Objects.requireNonNull(item);
        item.setActive(Boolean.FALSE);
        item.setStatus(FmConstant.Status.INVALID);
        // To
        to.setId(null);
        to.setBillId(item.getBillId());
        to.setSerial(item.getSerial() + "R");
        to.setCode(item.getCode() + "R");
        to.setStatus(FmConstant.Status.INVALID);
        to.setRelatedId(item.getId());
        to.setIncome(item.getIncome());
        Ke.umCreated(to, item);
    }
}
