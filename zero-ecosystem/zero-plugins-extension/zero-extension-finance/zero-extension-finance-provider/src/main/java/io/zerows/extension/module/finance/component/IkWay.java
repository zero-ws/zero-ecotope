package io.zerows.extension.module.finance.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.domain.tables.pojos.FBill;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FBook;
import io.zerows.extension.module.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.module.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FTrans;
import io.zerows.extension.module.finance.domain.tables.pojos.FTransItem;
import io.zerows.platform.annotations.meta.Memory;
import io.zerows.platform.exception._80413Exception501NotImplement;

import java.util.List;

/**
 * 通道接口，主要用于设置数据信息专用接口，替换旧版的 Fill Service
 * 中的设置程序，将服务逻辑抽象并且压入底层执行处理，以更加细粒度的方式来
 * 完成数据的拷贝动作。编号规则如：
 * <pre><code>
 *        {@link IkBill2BillItem}
 *        账单编号:              CODE
 *        账单明细编号:           - CODE-01
 *                              - CODE-02
 *        预授权编号：            CODE-A
 * </code></pre>
 * 账单数据处理，账单的核心属性信息需要在本接口详细说明，最基础的属性如下：
 * <pre><code>
 *     status
 *     - Pending：{@link FmConstant.Status#PENDING}
 *       等待处理的账单，即等待结算的账单处理。
 *     - Finished：{@link FmConstant.Status#FINISHED}
 *       已经处理完成的账单，这种账单已经产生了结算单。
 *     - InValid：{@link FmConstant.Status#INVALID}
 *       （无效账单）若出现了转账、拆账、冲账，那么原始的账单的状态就是无效。
 *     - Fixed：{@link FmConstant.Status#FIXED}
 *       固定账单，目前表示哑房账的状态（未来可能直接被拿掉）。
 *     - Valid：{@link FmConstant.Status#VALID}
 *       （有效账单）结算过程中必须是有效账单被提取。
 * </code></pre>
 * <p>
 * 由于在账务管理系统中已经存在一个基本概念：账单项和会计科目，所以此处账单下边关联的项目
 * 一律使用术语：子项（而不是账单项），方便开发人员理解子项对应的含义，并且区分账单项和
 * 子项的区别：
 * - 账单项，Pay Term，表示当前账单所在的分类树的基础信息。
 * - 子项，Bill Item，表示属于当前账单下的项列表。
 * </p>
 * 共享规则如下：
 * <pre><code>
 *     1. code / serial 同时表示编号，目前版本所有单据一致
 *     2. 所属部分使用 Key 主键关联
 *     3. 若无特殊说明，status = Pending
 *     4. 所有的 auditor 信息直接从 输入的内容中提取
 * </code></pre>
 * 结构如下：
 * <pre><code>
 *     账单        结算单         付款单
 *       \        /    \        /
 *        \      /      \      /
 *         \    /        \    /
 *        账单子项         付款项
 *        billId       settlementId
 *     settlementId      paymentId
 *     账单 - 结算单 - 付款单 都是多对多的关联关系，并且依赖子项做关联
 *     形成异构的结构，结算单和付款单都是基于子项的关联关系。
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
@SuppressWarnings("all")
public interface IkWay<INPUT, OUTPUT> {

    @Memory(IkWay.class)
    Cc<String, IkWay> CC_SKELETON = Cc.openThread();

    @SuppressWarnings("unchecked")
    static IkWay<FBill, FBillItem> ofB2BI() {
        // Bill -> BillItem, Bill -> List<BillItem>
        return (IkWay<FBill, FBillItem>) CC_SKELETON.pick(IkBill2BillItem::new, IkBill2BillItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<FBill, FPreAuthorize> ofB2A() {
        // Bill -> PreAuthorize
        return (IkWay<FBill, FPreAuthorize>) CC_SKELETON.pick(IkBill2PreAuthorize::new, IkBill2PreAuthorize.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<FBillItem, FBillItem> ofBIS() {
        // BillItem -> BillItem
        return (IkWay<FBillItem, FBillItem>) CC_SKELETON.pick(IkBillItemSplit::new, IkBillItemSplit.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<FBillItem, FBillItem> ofBIR() {
        // BillItem -> BillItem
        return (IkWay<FBillItem, FBillItem>) CC_SKELETON.pick(IkBillItemRevert::new, IkBillItemRevert.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<List<FBillItem>, FBillItem> ofBIT() {
        // BillItem -> BillItem
        return (IkWay<List<FBillItem>, FBillItem>) CC_SKELETON.pick(IkBillItemTransfer::new, IkBillItemTransfer.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<FBook, FBill> ofBKT() {
        // Book -> Bill
        return (IkWay<FBook, FBill>) CC_SKELETON.pick(IkBook2Bill::new, IkBook2Bill.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<FBillItem, JsonObject> ofBIC() {
        // BillItem -> JsonObject
        return (IkWay<FBillItem, JsonObject>) CC_SKELETON.pick(IkBillItemCancel::new, IkBillItemCancel.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<FSettlement, FBillItem> ofST2BI() {
        // Settlement -> BillItem
        return (IkWay<FSettlement, FBillItem>) CC_SKELETON.pick(IkSettlement2BillItem::new, IkSettlement2BillItem.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<List<FSettlementItem>, FDebt> ofSI2D() {
        // List<FSettlementItem> -> Debt
        return (IkWay<List<FSettlementItem>, FDebt>) CC_SKELETON.pick(IkSettlementItem2Debt::new, IkSettlementItem2Debt.class.getName());
    }

    @SuppressWarnings("unchecked")
    static IkWay<FTrans, FTransItem> ofT2TI() {
        // Trans -> TransItem
        return (IkWay<FTrans, FTransItem>) CC_SKELETON.pick(IkTrans2TransItem::new, IkTrans2TransItem.class.getName());
    }

    default void transfer(final INPUT input, final OUTPUT output) {
        throw new _80413Exception501NotImplement();
    }

    default void transfer(final INPUT input, final List<OUTPUT> outputs) {
        throw new _80413Exception501NotImplement();
    }
}