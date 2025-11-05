package io.zerows.extension.module.finance.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.component.Book;
import io.zerows.extension.module.finance.component.IkWay;
import io.zerows.extension.module.finance.component.Maker;
import io.zerows.extension.module.finance.domain.tables.daos.FBillItemDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.servicespec.InCancelStub;
import io.zerows.program.Ux;

/**
 * @author lang : 2024-01-11
 */
public class InCancelService implements InCancelStub {

    @Override
    public Future<Boolean> cancelAsync(final JsonArray keys, final JsonObject params) {
        final JsonObject condition = Ux.whereAnd();
        condition.put("key,i", keys);
        final ADB jq = DB.on(FBillItemDao.class);
        return jq.<FBillItem>fetchAsync(condition).compose(queried -> {
            queried.forEach(each -> IkWay.ofBIC().transfer(each, params));
            return jq.updateAsync(queried).compose(Book.of()::income);
        });
    }

    @Override
    public Future<Boolean> cancelAsync(final JsonArray keys, final String key, final JsonObject params) {
        final JsonObject condition = Ux.whereAnd();
        condition.put("key,i", keys);
        final JsonObject updated = new JsonObject();
        updated.put(KName.UPDATED_AT, params.getValue(KName.UPDATED_AT));
        updated.put(KName.UPDATED_BY, params.getValue(KName.UPDATED_BY));
        updated.put(KName.ACTIVE, Boolean.TRUE);
        updated.put(KName.STATUS, FmConstant.Status.PENDING);
        return DB.on(FBillItemDao.class).deleteByAsync(condition)
            .compose(nil -> Maker.upBI().buildAsync(updated, key))
            .compose(DB.on(FBillItemDao.class)::updateAsync)
            .compose(nil -> Ux.futureT());
    }
}
