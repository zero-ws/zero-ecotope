package io.zerows.extension.commerce.finance.agent.service.end;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.commerce.finance.domain.tables.daos.FDebtDao;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementDao;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.commerce.finance.eon.em.EmDebt;
import io.zerows.extension.commerce.finance.eon.em.EmTran;
import io.zerows.extension.commerce.finance.util.Fm;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SettleRService implements SettleRStub {

    @Inject
    private transient TransStub transStub;

    /**
     * 此处注意响应的格式，响应格式在原始基础上会有所变化
     * <pre><code>
     *     1. 原来的单结算单的模式改成了多结算单模式，那么此处有可能会出现多张订单同时结算的情况
     *        结算限制：结算本身不限制，只是给出相关提示，同订单的选择提示。
     *     2. 响应数据格式如：
     *        {
     *            "settlements": [],
     *            "items": [],
     *            "debts": [],
     *            "transactions": []
     *        }
     *        前端去针对数据结构做运算，此处的结构是为了方便前端处理，后端不做任何运算，此处是在
     *        选择之后提取 settlements 结算信息时出来的内容，放前端运算更靠谱，和结算相关的信息
     *        有两个维度
     *        - debts：相关应收信息
     *        - transactions：交易数据，交易数据中会包含直接结算部分，如果交易数据直接和结算挂钩
     *        证明这些交易数据本身是关联结算的，而且是直接结算模式。
     * </code></pre>
     *
     * @param keys 传入的结算单主键集合
     *
     * @return JsonObject
     */
    @Override
    public Future<JsonObject> fetchSettlement(final JsonArray keys) {
        final JsonObject response = new JsonObject();
        return Ux.Jooq.on(FSettlementDao.class).<FSettlement>fetchInAsync(KName.KEY, keys)
            .compose(this::statusSettlement)
            .compose(settlementA -> {
                /* settlements */
                response.put(KName.Finance.SETTLEMENTS, settlementA);
                return this.fetchInternalItems(keys);
            })
            .compose(items -> {
                /* items */
                response.put(KName.ITEMS, Ux.toJson(items));
                final JsonArray debtIds = Ut.toJArray(Ut.valueSetString(items, FSettlementItem::getDebtId));
                return Ux.Jooq.on(FDebtDao.class).fetchInAsync(KName.KEY, debtIds);
            })
            .compose(debts -> {
                /* debts */
                response.put(KName.Finance.DEBTS, Ux.toJson(debts));
                return this.transStub.fetchAsync(Ut.toSet(keys), Set.of(EmTran.Type.SETTLEMENT));
            })
            .compose(tranData -> Ux.future(Fm.toTransaction(response, tranData)));
    }

    /**
     * 根据交易ID读取结算单列表，结算单列表中会包含 items 属性用来存储结算单相关的结算明细信息
     * <pre><code>
     *     type = SETTLEMENT
     * </code></pre>
     *
     * @param transId 交易ID
     *
     * @return 结算单列表
     */
    @Override
    public Future<JsonArray> fetchByTran(final String transId) {
        final JsonObject cond = Ux.whereAnd();
        return null;
    }

    @Override
    public Future<JsonArray> statusSettlement(final JsonArray settlements) {
        final JsonArray keys = Ut.valueJArray(settlements, KName.KEY);
        return this.fetchStatus(keys).compose(statusMap -> {
            /*
             * statusMap 中的数据结构如：
             * - settlementId = JsonArray
             * 其中 JsonArray 中的状态值会有多个，如果为空，则表示没有处理过
             * 那么状态中的数据应该是 PENDING（单元素）
             */
            Ut.itJArray(settlements).forEach(settleJ -> {
                final String key = settleJ.getString(KName.KEY);
                final JsonArray status = statusMap.getOrDefault(key, new JsonArray());
                settleJ.put(KName.LINKED, status);
            });
            return Ux.future(settlements);
        });
    }

    @Override
    public Future<JsonArray> statusSettlement(final List<FSettlement> settlements) {
        final JsonArray settlementA = Ux.toJson(settlements);
        //  旧版：final JsonArray keys = Ut.toJArray(Ut.elementSet(settlements, FSettlement::getKey));
        return this.statusSettlement(settlementA);
    }

    /**
     * 完整的状态计算，此处计算结算单状态
     * <pre><code>
     *     1. 根据结算单中数据提取状态相关信息，先读取结算明细，然后根据结算明细进行分析。
     *        Settlement      Debt
     *                \        /
     *                 \      /
     *              Settlement Item
     *     2. 结算管理中的状态信息说明
     *        - finished = false            PENDING        -> 未完成
     *        - finished = true
     *          - debtId = null             SETTLEMENT     -> 结算完成
     *          - debtId != null            应收或退款
     *            - debt金额 > 0             DEBT           -> 应收
     *            - debt金额 < 0             REFUND         -> 应退
     *     3. 结算单是否完成的计算
     *        - 结算明细 finishedId 有值      debtId = null   -> 直接结算完成
     *                                      debtId !- null  -> 应收/应退处理（应收单中继续处理）
     *        - 结算明细 finishedId = null   此时 debtId 不可以有值，有值证明处理过
     *        - 所有结算明细 finishedId 有值，则结算单完成，否则结算单依旧挂起
     *     4. 有了结算明细的状态计算之后，上层计算已经没有任何作用了，直接在结算明细中考虑相关问题
     *        关于结算明细中的关联属性说明
     *        - settlementId：当前明细所属结算单的关联ID
     *        - debtId：当前明细所属应收/应退单的关联ID
     *        - relatedId：当前结算明细对应的 1:1 的账单明细关联ID
     *        - finishedId：当前结算明细关联的完成ID（完成ID可以是交易ID，存在交易ID证明已被处理过）
     *        新版移除了 finished 和 finishedAt 的信息，因为结算明细中的 finished 可直接通过 finishedId 判断，
     *        而结算明细中的 finishedAt 则可以直接使用挂靠的 finishedId 对应单据的 updatedAt 来计算。
     * </code></pre>
     *
     * @param keys 传入的结算单主键集合
     *
     * @return 返回“结算单”对应的状态信息
     */
    public Future<ConcurrentMap<String, JsonArray>> fetchStatus(final JsonArray keys) {
        final JsonArray settlementIds = Ut.toJArray(keys);
        final ConcurrentMap<String, JsonArray> statusMap = new ConcurrentHashMap<>();
        return this.fetchInternalItems(settlementIds).compose(items -> {
            final ConcurrentMap<String, List<FSettlementItem>> mapSettle = Ut.elementGroup(items, FSettlementItem::getSettlementId);

            /*
             * 追加结算单基本状态
             * - PENDING：未开始结算
             * - DONE：已完成结算
             * - PART：部分完成结算
             */
            Ut.itJString(keys).forEach(settlementId -> {
                final List<FSettlementItem> groupItems = mapSettle.getOrDefault(settlementId, new ArrayList<>());
                if (!groupItems.isEmpty()) {
                    final JsonArray statusQ = new JsonArray();
                    // 计算 PENDING / DONE / PART
                    final int size = groupItems.size();
                    final ArrayList<String> finishedSet = new ArrayList<>();
                    for (final FSettlementItem groupItem : groupItems) {
                        final String finishedId = groupItem.getFinishedId();
                        if (Ut.isNotNull(finishedId)) {
                            finishedSet.add(finishedId);
                        }
                    }
                    if (size == finishedSet.size()) {
                        // DONE
                        statusQ.add(EmDebt.Linked.DONE.name());
                    } else if (finishedSet.isEmpty()) {
                        // PENDING
                        statusQ.add(EmDebt.Linked.PENDING.name());
                    } else {
                        // PART
                        statusQ.add(EmDebt.Linked.PART.name());
                    }
                    statusMap.put(settlementId, statusQ);
                }
            });

            /*
             * 计算结算明细中的 debtId 来判断结算单中会包含额外的状态
             * - DEBT：有应收
             * - REFUND：有应退
             */
            final Set<String> debtIds = items.stream()
                .map(FSettlementItem::getDebtId)
                .filter(Ut::isNotNil)
                .collect(Collectors.toSet());
            return Ux.Jooq.on(FDebtDao.class).<FDebt>fetchInAsync(KName.KEY, debtIds).compose(debts -> {
                final ConcurrentMap<String, List<FSettlementItem>> mapItem = Ut.elementGroup(items, FSettlementItem::getDebtId);
                debts.forEach(debt -> {
                    final String debtId = debt.getKey();
                    final List<FSettlementItem> itemList = mapItem.getOrDefault(debtId, new ArrayList<>());
                    itemList.forEach(item -> {
                        final JsonArray statusQ = statusMap.getOrDefault(item.getSettlementId(), new JsonArray());
                        if (Ut.isNotNil(statusQ)) {
                            if (EmDebt.Type.DEBT.name().equals(debt.getType())) {
                                // DEBT
                                statusQ.add(EmDebt.Linked.DEBT.name());
                            } else {
                                // REFUND
                                statusQ.add(EmDebt.Linked.REFUND.name());
                            }
                        }
                        statusMap.put(item.getSettlementId(), statusQ);
                    });
                });
                return Ux.future(statusMap);
            });
        });
    }

    // ---------------- Private -----------------

    private Future<List<FSettlementItem>> fetchInternalItems(final JsonArray settlementIds) {
        return Ux.Jooq.on(FSettlementItemDao.class).fetchInAsync(KName.Finance.SETTLEMENT_ID, settlementIds);
    }
}
