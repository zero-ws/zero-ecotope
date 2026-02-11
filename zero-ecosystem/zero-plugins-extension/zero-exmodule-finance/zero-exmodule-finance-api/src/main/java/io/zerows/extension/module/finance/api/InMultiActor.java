package io.zerows.extension.module.finance.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.finance.component.Maker;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.servicespec.InBillStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.List;

/**
 * @author lang : 2024-01-11
 */
@Queue
public class InMultiActor {

    @Inject
    private transient InBillStub billStub;

    /**
     * 参考：{@link InMultiAgent#inMulti} 接口注释
     */
    @Me
    @Address(Addr.Bill.IN_MULTI)
    public Future<JsonObject> inMulti(final JsonObject data) {
        final List<FBillItem> items = Ux.fromJson(Ut.valueJArray(data, KName.ITEMS), FBillItem.class);
        return Maker.ofB().buildFastAsync(data)
            /* 账单：1，账单明细：N */
            .compose(bill -> this.billStub.multiAsync(
                bill,                                   // 账单对象
                items                                   // 账单明细列表
            ));
    }
}
