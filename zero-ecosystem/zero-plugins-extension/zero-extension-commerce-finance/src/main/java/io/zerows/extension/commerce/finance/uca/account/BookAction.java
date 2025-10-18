package io.zerows.extension.commerce.finance.uca.account;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.finance.domain.tables.daos.FBillDao;
import io.zerows.extension.commerce.finance.domain.tables.daos.FBookDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBill;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBook;
import io.zerows.extension.commerce.finance.util.Fm;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-01-19
 */
class BookAction {
    // 账本金额修正（可重用，所以独立）
    static FBook doAmount(final FBook book, final FBill bill, final List<FBillItem> items) {
        items.forEach(item -> {
            BigDecimal bookDecimal = Objects.requireNonNull(book.getAmount());
            // Bill for `income` checking
            final BigDecimal adjust = Objects.requireNonNull(item.getAmount());
            bookDecimal = Fm.calcAmount(bookDecimal, adjust, bill.getIncome(), item.getStatus());
            book.setUpdatedAt(LocalDateTime.now());
            book.setUpdatedBy(item.getUpdatedBy());
            book.setAmount(bookDecimal);
        });
        return book;
    }

    static Future<ConcurrentMap<String, List<FBill>>> mapBills(final List<FBillItem> items) {
        final Set<String> billKeys = items.stream()
            .map(FBillItem::getBillId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        final JsonObject condition = new JsonObject();
        condition.put("key,i", Ut.toJArray(billKeys));
        return DB.on(FBillDao.class).<FBill>fetchAsync(condition).compose(bills -> {
            if (bills.isEmpty()) {
                return Ux.future(new ConcurrentHashMap<>());
            }
            return Ux.future(Ut.elementGroup(bills, FBill::getBookId, bill -> bill));
        });
    }

    static Future<ConcurrentMap<String, FBook>> mapBook(final Set<String> billKeys) {
        final JsonObject criteria = new JsonObject();
        criteria.put("key,i", Ut.toJArray(billKeys));
        return DB.on(FBookDao.class).<FBook>fetchAsync(criteria).compose(books -> {
            if (books.isEmpty()) {
                return Ux.future(new ConcurrentHashMap<>());
            }
            return Ux.future(Ut.elementMap(books, FBook::getKey));
        });
    }
}
