package io.zerows.extension.commerce.finance.uca.replica;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.eon.FmConstant;

import java.time.LocalDateTime;

/**
 * 撤销处理
 * <pre><code>
 *     1. {@link JsonObject} -> {@link FBillItem}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class BillItemCancel implements IkWay<FBillItem, JsonObject> {
    /**
     * 「撤销」撤销操作，直接变更当前账单的基础信息
     * <pre><code>
     *     1. active = false
     *     2. status = InValid
     *     3. type = Cancel （类型变更）
     * </code></pre>
     *
     * @param item   from = 账单项
     * @param params to = 撤销相关参数
     */
    @Override
    public void transfer(final FBillItem item, final JsonObject params) {
        item.setActive(Boolean.FALSE);
        item.setStatus(FmConstant.Status.INVALID);
        item.setType(FmConstant.Type.CANCEL);
        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedBy(params.getString(KName.UPDATED_BY));
    }
}
