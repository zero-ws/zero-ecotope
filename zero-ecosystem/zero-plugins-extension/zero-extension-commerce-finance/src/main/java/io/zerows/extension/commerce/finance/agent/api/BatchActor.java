package io.zerows.extension.commerce.finance.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.commerce.finance.agent.service.end.DebtStub;
import io.zerows.extension.commerce.finance.agent.service.end.SettleRStub;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementDao;
import io.zerows.extension.commerce.finance.eon.Addr;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-01-25
 */
@Queue
public class BatchActor {
    @Inject
    private transient SettleRStub settleRStub;

    @Inject
    private transient DebtStub debtStub;

    @Address(Addr.Settle.FETCH_BY_KEY)
    public Future<JsonObject> fetchSettlement(final JsonArray keys) {
        return Fx.ofJObject(this.settleRStub::fetchSettlement).apply(keys);
    }


    @Address(Addr.Settle.FETCH_BY_QR)
    public Future<JsonObject> searchSettle(final JsonObject qr) {
        return Ux.Jooq.on(FSettlementDao.class).searchAsync(qr).compose(pageData -> {
            final JsonArray settlementData = Ut.valueJArray(pageData, KName.LIST);
            return this.settleRStub.statusSettlement(settlementData).compose(settlementA -> {
                pageData.put(KName.LIST, settlementA);
                return Ux.future(pageData);
            });
        });
    }

    @Address(Addr.Settle.FETCH_DEBT)
    public Future<JsonObject> fetchDebt(final JsonArray keys) {
        return Fx.ofJObject(this.debtStub::fetchDebt).apply(keys);
    }
}
