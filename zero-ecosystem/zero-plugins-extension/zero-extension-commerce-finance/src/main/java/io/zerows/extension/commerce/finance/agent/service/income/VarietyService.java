package io.zerows.extension.commerce.finance.agent.service.income;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.support.Ut;
import io.zerows.extension.commerce.finance.domain.tables.daos.FBillDao;
import io.zerows.extension.commerce.finance.domain.tables.daos.FBillItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBill;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBook;
import io.zerows.extension.commerce.finance.uca.account.Book;
import io.zerows.extension.commerce.finance.uca.enter.Maker;
import io.zerows.extension.commerce.finance.uca.replica.IkWay;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-01-11
 */
public class VarietyService implements VarietyStub {

    @Override
    public Future<JsonObject> splitAsync(final FBillItem item, final List<FBillItem> items) {
        // UCA
        IkWay.ofBIS().transfer(item, items);

        final UxJooq jooq = Ux.Jooq.on(FBillItemDao.class);
        return jooq.updateAsync(item)
            .compose(nil -> jooq.insertAsync(items))
            .compose(nil -> Ux.futureJ(item));
    }

    @Override
    public Future<JsonObject> revertAsync(final FBillItem item, final FBillItem to) {
        // UCA
        IkWay.ofBIR().transfer(item, to);

        final UxJooq jooq = Ux.Jooq.on(FBillItemDao.class);
        return jooq.updateAsync(item)
            .compose(nil -> jooq.insertAsync(to))
            .compose(nil -> Ux.Jooq.on(FBillDao.class).<FBill>fetchByIdAsync(to.getBillId()))
            .compose(bill -> Book.of().income(bill, to))
            .compose(nil -> Ux.futureJ(item));
    }

    @Override
    public Future<JsonObject> transferAsync(final ConcurrentMap<Boolean, List<FBillItem>> fromTo, final FBook book,
                                            final JsonObject params) {
        /*
         * `comment` from params
         *  1. Bill set comment
         *  2. Bill Item set comment ( newItem )
         */
        final String comment = params.getString(KName.COMMENT);
        return Maker.ofB().buildFastAsync(params).compose(preBill -> {
            // UCA
            IkWay.ofBKT().transfer(book, preBill);

            preBill.setComment(comment);
            return Ux.Jooq.on(FBillDao.class).insertAsync(preBill).compose(bill -> {
                    // FBillItem New Adding
                    final List<FBillItem> newItem = fromTo.get(Boolean.TRUE);
                    newItem.forEach(each -> {
                        each.setBillId(bill.getKey());

                        each.setComment(VString.ARROW_RIGHT + comment);
                    });
                    return Ux.Jooq.on(FBillItemDao.class).insertAsync(newItem)
                        .compose(items -> Book.of().income(bill, items));
                }).compose(added -> {
                    // FBillItem Previous Updating
                    final List<FBillItem> oldItem = fromTo.get(Boolean.FALSE);

                    oldItem.forEach(each -> {
                        final String previous = each.getComment();
                        if (Ut.isNil(previous)) {
                            each.setComment(comment + VString.ARROW_RIGHT);
                        } else {
                            each.setComment(previous + VString.ARROW_RIGHT + comment);
                        }
                    });
                    return Ux.Jooq.on(FBillItemDao.class).updateAsync(oldItem);
                }).compose(Book.of()::income)
                .compose(nil -> Ux.futureJ(preBill));
        });

    }
}
