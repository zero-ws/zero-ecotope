package io.zerows.extension.commerce.finance.agent.service.income;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.extension.commerce.finance.domain.tables.daos.FBillItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.commerce.finance.uca.account.Book;
import io.zerows.extension.commerce.finance.uca.enter.Maker;
import io.zerows.extension.commerce.finance.uca.replica.IkWay;
import io.zerows.epoch.corpus.Ux;

/**
 * @author lang : 2024-01-11
 */
public class CancelService implements CancelStub {

    @Override
    public Future<Boolean> cancelAsync(final JsonArray keys, final JsonObject params) {
        final JsonObject condition = Ux.whereAnd();
        condition.put("key,i", keys);
        final UxJooq jq = Ux.Jooq.on(FBillItemDao.class);
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
        return Ux.Jooq.on(FBillItemDao.class).deleteByAsync(condition)
            .compose(nil -> Maker.upBI().buildAsync(updated, key))
            .compose(Ux.Jooq.on(FBillItemDao.class)::updateAsync)
            .compose(nil -> Ux.futureT());
    }
}
