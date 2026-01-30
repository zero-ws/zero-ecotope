package io.zerows.extension.module.finance.component;

import io.zerows.extension.module.finance.domain.tables.pojos.FBill;
import io.zerows.extension.module.finance.domain.tables.pojos.FBook;

/**
 * 转账专用，一个核心方法
 * <pre><code>
 *     1. {@link FBook} -> {@link FBill}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class IkBook2Bill implements IkWay<FBook, FBill> {

    /**
     * 「转账」转账过程中填充账单信息专用，直接将账本信息复制到账单
     * <pre><code>
     *     1. bookId：关联账本主键
     *     2. orderId：关联订单主键
     *     3. modelId / modelKey：广义关联模型
     *     4. 新账单需要清空 key 主键（新建时自动生成）
     * </code></pre>
     *
     * @param book from = 账本
     * @param bill to = 账单
     */
    @Override
    public void transfer(final FBook book, final FBill bill) {
        bill.setId(null);
        bill.setBookId(book.getId());
        bill.setOrderId(book.getOrderId());
        bill.setModelId(book.getModelId());
        bill.setModelKey(book.getModelKey());
        // Created
        bill.setCreatedAt(bill.getUpdatedAt());
        bill.setCreatedBy(bill.getUpdatedBy());
    }
}
