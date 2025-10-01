package io.zerows.extension.commerce.finance.agent.service.end;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.common.shared.program.KRef;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.commerce.finance.eon.em.EmPay;
import io.zerows.extension.commerce.finance.uca.trans.Trade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lang : 2024-01-23
 */
public class SettleWService implements SettleWStub {
    /**
     * 结算步骤
     * <pre><code>
     *      结算单 x 1
     *      结算明细 x N
     * </code></pre>
     *
     * @param body 结算数据
     * @param type 结算类型
     *
     * @return 结算结果
     */
    @Override
    public Future<FSettlement> createAsync(final JsonObject body, final EmPay.Type type) {
        // 01. 创建结算单, type -> FSettlement
        final KRef settleRef = new KRef();
        // (JsonObject, EmPay.Type) -> FSettlement
        return Trade.step01ST().flatter(body, type)
            .compose(settleRef::future)


            // 02. 根据结算单处理账单明细，此处外层调用 updateAsync 更新方法（防并发冲突）
            // (JsonObject, FSettlement) -> List<FBillItem>
            .compose(inserted -> Trade.step02BI().scatter(body, inserted))


            // 03. 执行关闭 Book 的操作（录入关闭）
            // (JsonObject, List<FBillItem>) -> List<FBillItem>
            .compose(items -> Trade.step03B().scatter(body, items))


            // 04. 根据 BillItems 构造 SettlementItems 记录并插入
            // (JsonArray, FSettlement) -> List<FSettlementItem>
            .compose(items -> Trade.step04SI().scatter(Ux.toJson(items), settleRef.get()))


            .compose(nil -> Ux.future(settleRef.get()));
    }

    /**
     * 结算步骤
     * <pre><code>
     *      结算单 x N
     *      结算明细 x N
     * </code></pre>
     *
     * @param body 结算数据
     * @param type 结算类型
     *
     * @return 结算结果
     */
    @Override
    public Future<List<FSettlement>> createAsync(final JsonArray body, final EmPay.Type type) {
        // 01. 创建结算单, type -> FSettlement
        final KRef settleRef = new KRef();
        // (JsonArray, EmPay.Type) -> List<FSettlement>
        return Trade.step01ST().scatter(body, type)
            .compose(settleRef::future)


            // 02. 根据结算单处理账单明细，此处外层调用 updateAsync 更新方法（防并发冲突）
            // (JsonArray, List<FSettlement>) -> List<FBillItem>
            .compose(inserted -> Trade.step02BI().flatter(body, inserted))


            // 03. 执行关闭 Book 的操作（录入关闭）
            // (JsonArray, List<FBillItem>) -> List<FBillItem>
            .compose(items -> Trade.step03B().scatter(body, items))


            // 04. 根据 BillItems 构造 SettlementItems 记录并插入
            // (JsonArray, List<FSettlement>) -> List<FSettlementItem>
            .compose(items -> Trade.step04SI().flatter(Ux.toJson(items), settleRef.get()))


            .compose(nil -> Ux.future(settleRef.get()));
    }

    @Override
    public Future<List<FSettlement>> updateAsync(final JsonObject body, final User user) {
        /*
         * 提取结算单基础数据，结算单在前端因为选择的关系，此处提供的结算单和结算明细会包含如下关系
         * 1. 选择的结算明细一定会包含在结算单中
         * 2. 结算明细可能出现不完全选择（半选模式）
         */
        final JsonArray settlementData = Ut.valueJArray(body, KName.Finance.SETTLEMENTS);
        final JsonArray settlementIds = Ut.valueJArray(settlementData, KName.KEY);


        /* 读取系统中对应的所有结算明细执行计算 */
        return Ux.Jooq.on(FSettlementItemDao.class).<FSettlementItem>fetchInAsync(FmConstant.ID.SETTLEMENT_ID, settlementIds)
            .compose(items -> {
                /* 输入的结算明细ID合集 */
                final JsonArray itemsIn = Ut.valueJArray(body, KName.ITEMS);
                final Set<String> itemsKeys = Ut.valueSetString(itemsIn, KName.KEY);

                /*
                 * 根据未处理的结算明细计算待更新的结算单
                 * itemsKeys: 输入的结算明细ID合集
                 * items：系统中对应的所有结算明细
                 * 计算方法如下：
                 *     遍历 items，若 items 中的记录没有出现在 itemsKeys 中则代表此结算单未完结，则不做更新处理
                 * 步骤：
                 *     settlementSet - ignoredSet = updatedIds
                 **/
                final Set<String> ignoreSet = new HashSet<>();
                items.stream()
                    .filter(item -> item.getDebtId() == null)
                    .filter(item -> !itemsKeys.contains(item.getKey()))
                    .forEach(item -> ignoreSet.add(item.getSettlementId()));
                final Set<String> settlementSet = Ut.toSet(settlementIds);
                return Ux.future(Ut.elementDiff(settlementSet, ignoreSet));
            })
            .compose(keys -> {

                /*
                 * 从 settlementData 中执行过滤，只保留 keys 中的结算单数据
                 * 这些结算单数据执行更新操作
                 * - finished           = true
                 * - finishedAt         = 当前时间
                 * - updatedBy          = 当前用户
                 * - updatedAt          = 当前时间
                 */
                final JsonArray updatedData = new JsonArray();
                Ut.itJArray(settlementData)
                    .filter(settlementJ -> keys.contains(Ut.valueString(settlementJ, KName.KEY)))
                    .forEach(updatedData::add);
                return Trade.sync01ST().scatter(updatedData, user);
            })
            .compose(settlements -> {
                /*
                 * 还原过滤的最终结果，此处可以不关心完成
                 */
                final List<FSettlement> settlementList = Ux.fromJson(settlementData, FSettlement.class);
                return Ux.future(settlementList);
            });
    }
}
