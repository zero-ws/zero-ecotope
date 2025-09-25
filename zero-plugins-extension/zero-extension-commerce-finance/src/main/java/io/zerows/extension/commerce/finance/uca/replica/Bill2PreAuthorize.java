package io.zerows.extension.commerce.finance.uca.replica;

import io.zerows.extension.commerce.finance.domain.tables.pojos.FBill;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.runtime.skeleton.refine.Ke;

import java.util.Objects;

/**
 * 一个核心方法
 * <pre><code>
 *     1. {@link FBill} -> {@link FPreAuthorize}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class Bill2PreAuthorize implements IkWay<FBill, FPreAuthorize> {
    /**
     * 「入账」设置账单的预授权信息，每一个账单只能有一个预授权信息与之关联，简单说预授权和账单的
     * 关系是 1:1 的基本关系，基础规则如：
     * <pre><code>
     *     1. 子项编号：CODE-A
     * </code></pre>
     * 执行步骤
     * <pre><code>
     *     1. billId 挂载
     *     2. 序号计算，预授权序号 = 账单序号-A
     *     3. 状态：PENDING
     *     6. createdAt / createdBy
     *        updatedAt / updatedBy
     * </code></pre>
     *
     * @param bill      from = 账单
     * @param authorize to = 预授权
     */
    @Override
    public void transfer(final FBill bill, final FPreAuthorize authorize) {
        if (Objects.isNull(authorize)) {
            return;
        }

        authorize.setBillId(bill.getKey());
        authorize.setSerial(bill.getSerial() + "-A");
        authorize.setCode(bill.getCode() + "-A");
        authorize.setStatus(FmConstant.Status.PENDING);
        // active, sigma
        Ke.umCreated(authorize, bill);
    }
}
