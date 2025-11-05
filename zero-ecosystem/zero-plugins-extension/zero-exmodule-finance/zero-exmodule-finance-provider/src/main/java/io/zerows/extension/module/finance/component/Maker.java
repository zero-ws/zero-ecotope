package io.zerows.extension.module.finance.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.finance.domain.tables.pojos.FBill;
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
 * 对象构造器，直接根据数据构造对象
 * <pre><code>
 *     1. 单对象构造
 *     2. 多对象构造
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
@SuppressWarnings("all")
public interface Maker<H, T> extends MakerOn<H, T> {


    @Memory(Maker.class)
    Cc<String, Maker> CC_SKELETON = Cc.openThread();

    // --------------- 创建新的 -------------------
    @SuppressWarnings("unchecked")
    static Maker<String, FBill> ofB() {
        return (Maker<String, FBill>) CC_SKELETON.pick(MakerBill::new, MakerBill.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Maker<String, FSettlement> ofST() {
        return (Maker<String, FSettlement>) CC_SKELETON.pick(MakerSettlement::new, MakerSettlement.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Maker<List<FBillItem>, FBillItem> ofBIT() {
        return (Maker<List<FBillItem>, FBillItem>) CC_SKELETON.pick(MakerBillItemTransfer::new, MakerBillItemTransfer.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Maker<FSettlement, FBillItem> ofBI() {
        return (Maker<FSettlement, FBillItem>) CC_SKELETON.pick(MakerBillItem::new, MakerBillItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Maker<List<FSettlementItem>, FDebt> ofD() {
        return (Maker<List<FSettlementItem>, FDebt>) CC_SKELETON.pick(MakerDebt::new, MakerDebt.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Maker<String, FTrans> ofT() {
        return (Maker<String, FTrans>) CC_SKELETON.pick(MakerTrans::new, MakerTrans.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Maker<FTrans, List<FTransOf>> ofTO() {
        return (Maker<FTrans, List<FTransOf>>) CC_SKELETON.pick(MakerTransOf::new, MakerTransOf.class.getName());
    }


    // --------------- 更新专用 -------------------
    @SuppressWarnings("unchecked")
    static Maker<String, FBillItem> upBI() {
        return (Maker<String, FBillItem>) CC_SKELETON.pick(MakerUpBillItem::new, MakerUpBillItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Maker<FSettlement, FSettlementItem> upSTI() {
        return (Maker<FSettlement, FSettlementItem>) CC_SKELETON.pick(MakerUpSettlementItem::new, MakerUpSettlementItem.class.getName());
    }

    // --------------- 实例方法 -------------------
    default Future<T> buildFastAsync(final JsonObject data) {
        throw new _80413Exception501NotImplement();
    }
}

