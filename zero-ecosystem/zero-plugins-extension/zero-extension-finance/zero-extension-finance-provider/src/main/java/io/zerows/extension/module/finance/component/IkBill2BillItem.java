package io.zerows.extension.module.finance.component;

import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.domain.tables.pojos.FBill;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.support.Ut;

import java.util.List;

/**
 * 两个核心方法
 * <pre><code>
 *     1. {@link FBill} -> {@link FBillItem}
 *     2. {@link FBill} -> {@link List<FBillItem>}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class IkBill2BillItem implements IkWay<FBill, FBillItem> {
    /**
     * 「入账」单项账单设置，一个账单只有一个子项，基础规则
     * <pre><code>
     *     1. 子项编号只有一个：CODE-01（为了和多项统一）
     *     2. 多执行一点，由于是单项入账，所以此处会多一个：单价/数量 的设置，并且由于
     *        商品/赔偿等都属于多项入账，所以此处的数量一定是1，而单价一定是子项金额。
     * </code></pre>
     * 执行步骤
     * <pre><code>
     *     1. billId 挂载
     *     2. 序号计算，明细序号 = 账单序号-01
     *     3. 状态：PENDING
     *     4. income 设置（直接拷贝）
     *     5. 数量、单价、总价
     *     6. createdAt / createdBy
     *        updatedAt / updatedBy
     * </code></pre>
     *
     * @param bill 账单
     * @param item 账单明细
     */
    @Override
    public void transfer(final FBill bill, final FBillItem item) {
        item.setBillId(bill.getKey());
        item.setSerial(bill.getSerial() + "-01");
        item.setCode(bill.getCode() + "-01");
        item.setStatus(FmConstant.Status.PENDING);
        item.setIncome(bill.getIncome());
        // price, quanlity, total
        item.setPrice(item.getAmount());
        item.setQuantity(1);
        item.setAmountTotal(item.getAmount());
        Ke.umCreated(item, bill);
    }

    /**
     * 「入账」多项账单设置，一个账单带有多个子项，基础规则如：
     * <pre><code>
     *     1. 若账单编号是 CODE，那么子项编号如：
     *        - CODE-01
     *        - CODE-02
     *        .....
     *        - CODE-XX
     * </code></pre>
     * 执行步骤
     * <pre><code>
     *     1. billId 挂载
     *     2. 序号计算，明细序号 = 账单序号-0X
     *     3. 状态：PENDING
     *     4. income 设置（直接拷贝）
     *     5. createdAt / createdBy
     *        updatedAt / updatedBy
     * </code></pre>
     *
     * @param bill  from = 账单
     * @param items to = 子项列表
     */
    @Override
    public void transfer(final FBill bill, final List<FBillItem> items) {
        for (int idx = 0; idx < items.size(); idx++) {
            final FBillItem item = items.get(idx);
            final int number = (idx + 1);
            item.setBillId(bill.getKey());
            item.setSerial(bill.getSerial() + "-" + Ut.fromAdjust(number, 2));
            item.setCode(bill.getCode() + "-" + Ut.fromAdjust(number, 2));
            item.setAmountTotal(item.getAmount());
            item.setStatus(FmConstant.Status.PENDING);
            item.setIncome(bill.getIncome());
            // auditor
            Ke.umCreated(item, bill);
        }
    }
}
