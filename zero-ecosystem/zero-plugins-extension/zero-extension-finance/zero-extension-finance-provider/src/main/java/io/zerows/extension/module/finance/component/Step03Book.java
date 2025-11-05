package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 步骤三：根据提供的账单明细的数据
 *
 * @author lang : 2024-01-22
 */
class Step03Book implements Step<List<FBillItem>, FBillItem> {
    @Override
    public Future<List<FBillItem>> scatter(final JsonObject body, final List<FBillItem> upItems) {
        /*
         * 账单项更新之后，账本需重新记账，此处会有所有账本的keys，系统自动计算
         * 最终更新账本之后的状态：
         * - Pending：账本没有结算完成，还有待持续结算（部分结账）
         * - Finished：账本已经全部完成结算
         * 上述状态是系统自动计算，所以不用担心账本的同步问题。
         *
         * 此处的数据格式：
         * {
         *     "book": []
         * }
         */
        final JsonArray bookArray = Ut.valueJArray(body, KName.Finance.BOOK);
        final Set<String> bookKeys = Ut.toSet(bookArray);
        return Book.of().income(upItems, bookKeys).compose(nil -> Ux.future(upItems));
    }

    @Override
    public Future<List<FBillItem>> scatter(final JsonArray data, final List<FBillItem> upItems) {
        final Set<String> bookKeys = new HashSet<>();
        Ut.itJArray(data).forEach(body -> {
            final JsonArray bookArray = Ut.valueJArray(body, KName.Finance.BOOK);
            Ut.itJString(bookArray).forEach(bookKeys::add);
        });
        return Book.of().income(upItems, bookKeys).compose(nil -> Ux.future(upItems));
    }
}
