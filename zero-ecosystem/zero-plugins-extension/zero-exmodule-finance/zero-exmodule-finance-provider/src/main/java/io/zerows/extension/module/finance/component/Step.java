package io.zerows.extension.module.finance.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.extension.module.finance.common.em.EmPay;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FTrans;
import io.zerows.extension.module.finance.domain.tables.pojos.FTransOf;
import io.zerows.platform.annotations.meta.Memory;
import io.zerows.platform.exception._80413Exception501NotImplement;

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
@SuppressWarnings("all")
public interface Step<IN, OUT> {

    @Memory(Step.class)
    Cc<String, Step> CC_SKELETON = Cc.openThread();

    @SuppressWarnings("unchecked")
    static Step<EmPay.Type, FSettlement> step01ST() {
        return (Step<EmPay.Type, FSettlement>) CC_SKELETON.pick(Step01Settlement::new, Step01Settlement.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<FSettlement, FBillItem> step02BI() {
        return (Step<FSettlement, FBillItem>) CC_SKELETON.pick(Step02BillItem::new, Step02BillItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<List<FBillItem>, FBillItem> step03B() {
        return (Step<List<FBillItem>, FBillItem>) CC_SKELETON.pick(Step03Book::new, Step03Book.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<FSettlement, FSettlementItem> step04SI() {
        return (Step<FSettlement, FSettlementItem>) CC_SKELETON.pick(Step04SettlementItem::new, Step04SettlementItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<List<FSettlement>, FDebt> step05D() {
        return (Step<List<FSettlement>, FDebt>) CC_SKELETON.pick(Step05Debt::new, Step05Debt.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<List<FSettlement>, FTrans> step06T() {
        return (Step<List<FSettlement>, FTrans>) CC_SKELETON.pick(Step06TransSettle::new, Step06TransSettle.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<FTrans, FTransOf> step06TO() {
        return (Step<FTrans, FTransOf>) CC_SKELETON.pick(Step06TransOf::new, Step06TransOf.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<List<FDebt>, FTrans> step07T() {
        return (Step<List<FDebt>, FTrans>) CC_SKELETON.pick(Step07TransDebt::new, Step07TransDebt.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<User, FSettlement> sync01ST() {
        return (Step<User, FSettlement>) CC_SKELETON.pick(StepSync01Settlement::new, StepSync01Settlement.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Step<User, FDebt> sync01DT() {
        return (Step<User, FDebt>) CC_SKELETON.pick(StepSync01Debt::new, StepSync01Debt.class.getName());
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
