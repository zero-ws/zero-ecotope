package io.zerows.extension.commerce.finance.agent.service.income;

import io.zerows.extension.commerce.finance.domain.tables.pojos.FBill;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FPreAuthorize;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface BillStub {
    /**
     * 「单笔」账单入账
     * 本接口用于单笔账单入账流程，主要牵涉三个核心对象
     * <pre><code>
     *     1. 账单对象 {@link FBill}
     *     2. 账单明细对象 {@link FBillItem}
     *     3. 预授权对象 {@link FPreAuthorize}
     * </code></pre>
     * 这种入账模式下，账单对象和账单明细对象的数量比例是 1:1，即一个账单对应一个账单项，预授权
     * 对象存在与否会决定是否真正访问 F_PRE_AUTHORIZE 表。
     *
     * @param bill      账单
     * @param billItem  账单明细
     * @param authorize 授权信息
     *
     * @return {@link Future}
     */
    Future<JsonObject> singleAsync(FBill bill, FBillItem billItem, FPreAuthorize authorize);

    /**
     * 「单笔」账单入账
     * 针对单笔入账的一个重载方法，只有两个参数（不考虑预授权）的接口
     *
     * @param bill     账单
     * @param billItem 账单明细
     *
     * @return {@link Future}
     */
    default Future<JsonObject> singleAsync(final FBill bill, final FBillItem billItem) {
        return this.singleAsync(bill, billItem, null);
    }

    /**
     * 「多笔」账单入账
     *
     * @param bill  账单
     * @param items 账单明细
     *
     * @return {@link Future}
     */
    Future<JsonObject> multiAsync(FBill bill, List<FBillItem> items);
}
