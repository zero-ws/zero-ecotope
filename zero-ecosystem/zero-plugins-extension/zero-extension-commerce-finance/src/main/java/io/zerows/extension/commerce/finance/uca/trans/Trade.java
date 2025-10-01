package io.zerows.extension.commerce.finance.uca.trans;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.core.exception.web._80413Exception501NotImplement;
import io.zerows.epoch.annotation.Memory;
import io.zerows.extension.commerce.finance.domain.tables.pojos.*;
import io.zerows.extension.commerce.finance.eon.em.EmPay;

import java.util.List;

/**
 * 交易专用接口，处理账单之后的结算部分
 * <pre><code>
 *     1. 三种结算方式的处理
 *        - 直接结算
 *        - 延迟结算
 *        - 直接转应收
 *     2. 结算管理
 *     3. 应收、退款处理
 *     4. 开票处理
 * </code></pre>
 *
 * @author lang : 2024-01-19
 */
public interface Trade<IN, OUT> {

    @SuppressWarnings("unchecked")
    static Trade<EmPay.Type, FSettlement> step01ST() {
        return (Trade<EmPay.Type, FSettlement>) POOL.CCT_TRADE.pick(Step01Settlement::new, Step01Settlement.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<FSettlement, FBillItem> step02BI() {
        return (Trade<FSettlement, FBillItem>) POOL.CCT_TRADE.pick(Step02BillItem::new, Step02BillItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<List<FBillItem>, FBillItem> step03B() {
        return (Trade<List<FBillItem>, FBillItem>) POOL.CCT_TRADE.pick(Step03Book::new, Step03Book.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<FSettlement, FSettlementItem> step04SI() {
        return (Trade<FSettlement, FSettlementItem>) POOL.CCT_TRADE.pick(Step04SettlementItem::new, Step04SettlementItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<List<FSettlement>, FDebt> step05D() {
        return (Trade<List<FSettlement>, FDebt>) POOL.CCT_TRADE.pick(Step05Debt::new, Step05Debt.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<List<FSettlement>, FTrans> step06T() {
        return (Trade<List<FSettlement>, FTrans>) POOL.CCT_TRADE.pick(Step06TransSettle::new, Step06TransSettle.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<FTrans, FTransOf> step06TO() {
        return (Trade<FTrans, FTransOf>) POOL.CCT_TRADE.pick(Step06TransOf::new, Step06TransOf.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<List<FDebt>, FTrans> step07T() {
        return (Trade<List<FDebt>, FTrans>) POOL.CCT_TRADE.pick(Step07TransDebt::new, Step07TransDebt.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<User, FSettlement> sync01ST() {
        return (Trade<User, FSettlement>) POOL.CCT_TRADE.pick(Sync01Settlement::new, Sync01Settlement.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Trade<User, FDebt> sync01DT() {
        return (Trade<User, FDebt>) POOL.CCT_TRADE.pick(Sync01Debt::new, Sync01Debt.class.getName());
    }

    /*
     * 标准接口
     * - IN -> OUT               单到单
     * - List<IN> -> List<OUT>   多到多    （多到多会包含散开的情况）
     */
    default Future<OUT> flatter(final JsonObject data, final IN assist) {
        throw new _80413Exception501NotImplement();
    }

    default Future<List<OUT>> flatter(final JsonArray data, final List<IN> assist) {
        throw new _80413Exception501NotImplement();
    }

    /*
     * 散开接口
     * - IN -> List<OUT>         单到多的散开接口
     */
    default Future<List<OUT>> scatter(final JsonObject data, final IN assist) {
        throw new _80413Exception501NotImplement();
    }

    default Future<List<OUT>> scatter(final JsonArray data, final IN assist) {
        throw new _80413Exception501NotImplement();
    }
}

@SuppressWarnings("all")
interface POOL {
    @Memory(Trade.class)
    Cc<String, Trade> CCT_TRADE = Cc.openThread();
}
