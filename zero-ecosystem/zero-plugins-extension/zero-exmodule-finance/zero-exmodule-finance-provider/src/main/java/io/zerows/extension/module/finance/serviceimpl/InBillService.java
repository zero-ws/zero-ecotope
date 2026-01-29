package io.zerows.extension.module.finance.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.component.Book;
import io.zerows.extension.module.finance.component.IkWay;
import io.zerows.extension.module.finance.domain.tables.daos.FBillDao;
import io.zerows.extension.module.finance.domain.tables.daos.FBillItemDao;
import io.zerows.extension.module.finance.domain.tables.daos.FPreAuthorizeDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FBill;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.extension.module.finance.servicespec.InBillStub;
import io.zerows.program.Ux;
import io.zerows.support.Fx;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class InBillService implements InBillStub {

    @Override
    public Future<JsonObject> singleAsync(final FBill bill, final FBillItem billItem, final FPreAuthorize authorize) {
        if (Objects.nonNull(authorize)) {
            bill.setAmount(BigDecimal.ZERO);
            billItem.setAmount(BigDecimal.ZERO);
        }
        return DB.on(FBillDao.class).insertAsync(bill).compose(inserted -> {
            // UCA
            IkWay.ofB2BI().transfer(bill, billItem);

            final List<Future<JsonObject>> futures = new ArrayList<>();
            futures.add(DB.on(FBillItemDao.class).insertJAsync(billItem));
            if (Objects.nonNull(authorize)) {
                // UCA
                IkWay.ofB2A().transfer(bill, authorize);
                futures.add(DB.on(FPreAuthorizeDao.class).insertJAsync(authorize));
            }
            final List<FBillItem> itemList = new ArrayList<>();
            itemList.add(billItem);
            return Fx.combineA(futures)
                .compose(nil -> Book.of().income(bill, itemList))
                .compose(nil -> this.billAsync(bill, itemList));
        });
    }

    @Override
    public Future<JsonObject> multiAsync(final FBill bill, final List<FBillItem> items) {
        /*
         * Because Multi will be selected from item, it means that the items contains `key` here
         * We must remove `key` of items instead of duplicated key met here
         * Fix:
         */
        items.forEach(item -> item.setId(null));

        return DB.on(FBillDao.class).insertAsync(bill).compose(inserted -> {
            // UCA
            IkWay.ofB2BI().transfer(bill, items);

            return DB.on(FBillItemDao.class).insertJAsync(items)
                .compose(nil -> Book.of().income(bill, items))
                .compose(nil -> this.billAsync(bill, items));
        });
    }

    private Future<JsonObject> billAsync(final FBill bill, final List<FBillItem> items) {
        final JsonObject response = Ux.toJson(bill);
        response.put(KName.ITEMS, Ux.toJson(items));
        return Ux.future(response);
    }
}
