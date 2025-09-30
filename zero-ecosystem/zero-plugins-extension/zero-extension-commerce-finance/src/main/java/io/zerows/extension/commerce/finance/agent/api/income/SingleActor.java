package io.zerows.extension.commerce.finance.agent.api.income;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Me;
import io.zerows.core.annotations.Queue;
import io.zerows.extension.commerce.finance.agent.service.income.BillStub;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.extension.commerce.finance.eon.Addr;
import io.zerows.extension.commerce.finance.uca.enter.Maker;
import io.zerows.extension.commerce.finance.util.Fm;
import io.zerows.unity.Ux;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-01-11
 */
@Queue
public class SingleActor {

    @Inject
    private transient BillStub billStub;

    /** 参考：{@link SingleAgent#inPre} 接口注释 */
    @Me
    @Address(Addr.Bill.IN_PRE)
    public Future<JsonObject> inPre(final JsonObject data) {
        final FBillItem item = Ux.fromJson(data, FBillItem.class);
        final FPreAuthorize authorize = Fm.toAuthorize(data);
        return Maker.ofB().buildFastAsync(data) // 账单序号生成
            /* 账单：1，账单明细：1，预授权：1 or ? ( preAuthorize 节点）*/
            .compose(bill -> this.billStub.singleAsync(
                bill,                                   // 账单对象
                item,                                   // 账单明细对象
                authorize                               // 预授权对象
            ));
    }


    /** 参考：{@link SingleAgent#inCommon} 接口注释 **/
    @Me
    @Address(Addr.Bill.IN_COMMON)
    public Future<JsonObject> inCommon(final JsonObject data) {
        final FBillItem item = Ux.fromJson(data, FBillItem.class);
        return Maker.ofB().buildFastAsync(data) // 账单序号生成
            /* 账单：1，账单明细：1 */
            .compose(bill -> this.billStub.singleAsync(
                bill,                                   // 账单对象
                item                                    // 账单明细对象
            ));
    }
}
