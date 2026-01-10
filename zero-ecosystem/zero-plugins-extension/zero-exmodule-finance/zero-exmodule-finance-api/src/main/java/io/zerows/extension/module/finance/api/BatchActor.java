package io.zerows.extension.module.finance.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.domain.tables.daos.FSettlementDao;
import io.zerows.extension.module.finance.servicespec.EndDebtStub;
import io.zerows.extension.module.finance.servicespec.EndSettleRStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-01-25
 */
@Queue
public class BatchActor {
    @Inject
    private transient EndSettleRStub settleRStub;

    @Inject
    private transient EndDebtStub debtStub;

    @Address(Addr.Settle.FETCH_BY_KEY)
    public Future<JsonObject> fetchSettlement(final JsonArray keys) {
        if (Ut.isNil(keys)) {
            return Ux.futureJ();
        }
        return this.settleRStub.fetchSettlement(keys);
    }


    @Address(Addr.Settle.FETCH_BY_QR)
    public Future<JsonObject> searchSettle(final JsonObject qr) {
        return DB.on(FSettlementDao.class).searchJAsync(qr).compose(pageData -> {
            final JsonArray settlementData = Ut.valueJArray(pageData, KName.LIST);
            return this.settleRStub.statusSettlement(settlementData).compose(settlementA -> {
                pageData.put(KName.LIST, settlementA);
                return Ux.future(pageData);
            });
        });
    }

    @Address(Addr.Settle.FETCH_DEBT)
    public Future<JsonObject> fetchDebt(final JsonArray keys) {
        if (Ut.isNil(keys)) {
            return Ux.futureJ();
        }
        return this.debtStub.fetchDebt(keys);
    }
}
