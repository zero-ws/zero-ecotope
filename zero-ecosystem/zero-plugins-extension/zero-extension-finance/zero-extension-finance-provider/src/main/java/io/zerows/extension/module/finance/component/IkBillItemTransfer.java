package io.zerows.extension.module.finance.component;

import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;

import java.util.List;

/**
 * 转账专用，一个核心方法
 * <pre><code>
 *     1. {@link List<FBillItem>} -> {@link List<FBillItem>}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class IkBillItemTransfer implements IkWay<List<FBillItem>, FBillItem> {


    /**
     * 「转账」批量转账处理，拉平对齐的处理模式
     * <pre><code>
     *     转出项：
     *     1. active = false
     *     2. status = InValid
     *     3. type = TransferFrom （类型变更）
     *     4. 原项编号为：CODE-XX，那么子项编号为：
     *        CODE-XXF
     *
     *     转入项：
     *     1. 清空 key 主键（新建时自动生成）
     *     2. 清空 billId 的原始关联（转出的账单要和 income 断开关联）
     *     3. active = true
     *     4. status = Pending
     *     5. 原项编号为：CODE-XX，那么子项编号为：
     *        CODE-XXT
     * </code></pre>
     *
     * @param from from = 源账单
     * @param to   to = 目标账单
     */
    @Override
    public void transfer(final List<FBillItem> from, final List<FBillItem> to) {
        from.forEach(fromItem -> {
            fromItem.setActive(Boolean.FALSE);
            fromItem.setStatus(FmConstant.Status.INVALID);
            fromItem.setType(FmConstant.Type.TRANSFER_FROM);
            fromItem.setSerial(fromItem.getSerial() + "F");
            fromItem.setCode(fromItem.getCode() + "F");
        });
        to.forEach(toItem -> {
            toItem.setKey(null);
            toItem.setBillId(null);
            toItem.setSerial(toItem.getSerial() + "Tool");
            toItem.setCode(toItem.getCode() + "Tool");
            toItem.setStatus(FmConstant.Status.PENDING);
            toItem.setActive(Boolean.TRUE);
        });
    }
}
