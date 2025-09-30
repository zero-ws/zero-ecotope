package io.zerows.extension.commerce.finance.agent.service.end;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.finance.domain.tables.daos.FDebtDao;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementDao;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.commerce.finance.eon.em.EmTran;
import io.zerows.extension.commerce.finance.uca.trans.Trade;
import io.zerows.extension.commerce.finance.util.Fm;
import io.zerows.unity.Ux;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Set;

/**
 * @author lang : 2024-01-24
 */
public class DebtService implements DebtStub {

    @Inject
    private transient TransStub transStub;

    @Override
    public Future<FDebt> createAsync(final JsonObject data, final FSettlement settlement) {
        final List<FSettlement> settlements = List.of(settlement);
        return Trade.step05D().flatter(data, settlements);
    }

    @Override
    public Future<FDebt> createAsync(final JsonObject data, final List<FSettlement> settlements) {
        /*
         * 此处的逻辑有一个关键点，就是在 data 中提取底层所需的 keySelected，有了
         * 此操作之后，转应收才能够成功，否则转应收会变成一个伪命题。
         */
        final JsonArray items = Ut.valueJArray(data, KName.ITEMS);
        final JsonArray keys = Ut.toJArray(Ut.valueSetString(items, KName.KEY));
        final JsonObject params = data.copy().put("selected", keys);
        return Trade.step05D().flatter(params, settlements);
    }

    /**
     * 此处注意响应格式，格式在原始基础上会有所变化
     * <pre><code>
     *     1. 原来单应收处理转换成了多应收处理，那么此处会出现多张应收单的情况
     *        应收限制：应收单位必须是同一个，非同一个单位的应收单不允许合并。
     *     2. 响应数据格式如：
     *        {
     *            "debts": [],
     *            "transactions": [],
     *            "settlements": [],
     *            "items": []
     *        }
     *        前端自行针对数据结构做运算，所以后端不做任何处理。
     * </code></pre>
     *
     * @param keys 传入的应收/退款单集合
     *
     * @return JsonObject
     */
    @Override
    public Future<JsonObject> fetchDebt(final JsonArray keys) {
        final JsonObject response = new JsonObject();
        return Ux.Jooq.on(FSettlementItemDao.class).<FSettlementItem>fetchInAsync(FmConstant.ID.DEBT_ID, keys)
            .compose(items -> {
                /* items */
                response.put(KName.ITEMS, Ux.toJson(items));
                final Set<String> settlementIds = Ut.valueSetString(items, FSettlementItem::getSettlementId);
                return Ux.Jooq.on(FSettlementDao.class).fetchJInAsync(KName.KEY, settlementIds);
            })
            .compose(settlementA -> {
                /* settlements */
                response.put(KName.Finance.SETTLEMENTS, settlementA);
                return Ux.Jooq.on(FDebtDao.class).fetchInAsync(KName.KEY, keys);
            })
            .compose(debts -> {
                /* debts */
                response.put(KName.Finance.DEBTS, Ux.toJson(debts));
                return this.transStub.fetchAsync(Ut.toSet(keys), Set.of(EmTran.Type.DEBT, EmTran.Type.REFUND));
            })
            .compose(tranData -> Ux.future(Fm.toTransaction(response, tranData)));
    }

    @Override
    public Future<List<FDebt>> updateAsync(final JsonObject body, final User user) {
        /*
         * 提取应收中的基础数据，此处无选择，直接针对 debts 中的应收单下手
         * 应收会被更新
         *    - updatedBy / finishedAt
         *    - finished / finishedAt
         *    - amountBalance
         */
        final JsonArray debtData = Ut.valueJArray(body, KName.Finance.DEBTS);

        return Trade.sync01DT().scatter(debtData, user);
    }
}
