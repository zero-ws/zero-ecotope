package io.zerows.extension.commerce.finance.uca.account;

import io.vertx.core.Future;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBill;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 记账专用组件，实现部分处理成和数据库中的 {@link io.zerows.extension.commerce.finance.domain.tables.pojos.FBook} 对接
 * 在整个模式下可以更灵活，以防止记录过程中出错的情况，底层后期也可以开启缓存来完成记账部分的完整处理
 *
 * @author lang : 2024-01-19
 */
public interface Book {

    static Book of() {
        return BookStore.CCT_BOOK.pick(BookDatabase::new, BookDatabase.class.getName());
    }

    Future<Boolean> income(FBill bill, List<FBillItem> items);

    default Future<Boolean> income(final FBill bill, final FBillItem item) {
        return this.income(bill, List.of(item));
    }

    default Future<Boolean> income(final List<FBillItem> items) {
        return this.income(items, new HashSet<>());
    }

    Future<Boolean> income(List<FBillItem> items, Set<String> keysClosed);
}

