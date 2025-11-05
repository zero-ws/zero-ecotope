package io.zerows.extension.module.finance.servicespec;

import io.vertx.core.Future;
import io.zerows.extension.module.finance.domain.tables.pojos.FBill;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlementItem;

import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface FetchStub {
    /**
     * 根据 ER 模型图，账单对象 {@link FBill} 中包含了 orderId 来表示当前账单集合隶属于哪个订单，账单会出现两种关联模型
     * <pre><code>
     *     1. orderId 的关联模型，直接表示当前账单隶属于哪个订单，如果没有订单信息，账单则游离，无法归并到某个数据范围
     *        当然会出现这种没有订单和账单关联的情况，主要是游离于业务之外的账单提取。
     *     2. 广义关联：通过 modelId / modelKey 来关联，这种关联主要表示账单本身是对应到哪个模型，比如：
     *        - 餐饮模型中的餐饮账单
     *        - 采购模型中的采购账单
     *        - 酒店房间模型中的房间账单
     * </code></pre>
     * 两种关联模型都可以提供查询方案，但 orderId 由于本身范围比较大，所以使用它来查询是全范围的方式做查询
     * <pre><code>
     *     SQL 语句 WHERE 部分：
     *     WHERE ORDER_ID = ?
     * </code></pre>
     *
     * @param orderId 订单ID
     *
     * @return {@link io.vertx.core.Future}
     */
    Future<List<FBill>> fetchByOrder(String orderId);


    /**
     * 根据 ER 模型图，账单和账单明细是父子级关系，此处的查询是 多查多 的模式，即：
     * <pre><code>
     *     1. 输入是一个账单集合，而不是单个账单
     *     2. 输出是一个账单明细集合，而不是单个账单明细
     * </code></pre>
     * 内部逻辑十分简单，直接从账单中提取 billId 来查询账单明细即可。
     * <pre><code>
     *     SQL 语句 WHERE 部分：
     *     WHERE BILL_ID IN (?, ?, ?)
     * </code></pre>
     *
     * @param bills 账单列表
     *
     * @return {@link io.vertx.core.Future}
     */
    Future<List<FBillItem>> fetchByBills(List<FBill> bills);


    /**
     * 根据 ER 模型图，结算单和账单明细同样存在父子级关系，此处的查询是 多查多 的模式，即：
     * <pre><code>
     *     1. 输入是一个账单明细的集合，而不是单个账单集合
     *     2. 输出是一个结算单列表
     * </code></pre>
     * 内部逻辑十分简单，直接从账单明细中提取 settlementId 来查询结算单即可。
     * <pre><code>
     *     SQL 语句 WHERE 部分：
     *     WHERE KEY IN (?, ?, ?)
     * </code></pre>
     *
     * @param items 账单明细列表
     *
     * @return {@link io.vertx.core.Future}
     */
    Future<List<FSettlement>> fetchSettlements(List<FBillItem> items);


    /**
     * 根据 ER 模型图，结算单和结算明细有父子级关系，此处的查询是 多查多 的模式，即：
     * <pre><code>
     *     1. 输入是一个结算单的集合，而不是单个结算单
     *     2. 输出是一个结算明细列表
     * </code></pre>
     * 内部逻辑十分简单，直接从结算单中提取 key 来查询结算明细即可。
     * <pre><code>
     *     SQL 语句 WHERE 部分：
     *     WHERE SETTLEMENT_ID IN (?, ?, ?)
     * </code></pre>
     *
     * @param settlements 结算单列表
     *
     * @return {@link io.vertx.core.Future}
     */
    Future<List<FSettlementItem>> fetchBySettlements(List<FSettlement> settlements);
}
