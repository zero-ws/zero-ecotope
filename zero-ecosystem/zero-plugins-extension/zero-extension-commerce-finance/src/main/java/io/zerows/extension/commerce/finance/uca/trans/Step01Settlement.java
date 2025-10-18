package io.zerows.extension.commerce.finance.uca.trans;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.eon.em.EmPay;
import io.zerows.extension.commerce.finance.uca.enter.Maker;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 步骤一：结算单处理流程（原始流程），完整步骤：
 * <pre><code>
 *     1. 生成单号
 *     2. 根据是否延迟设定延迟相关信息
 *     3. 批量插入结算单
 * </code></pre>
 *
 * @author lang : 2024-01-19
 */
class Step01Settlement implements Trade<EmPay.Type, FSettlement> {
    // type -> FSettlement
    @Override
    public Future<FSettlement> flatter(final JsonObject data, final EmPay.Type type) {
        /*
         * 结算单创建时会生成单号，编号规则存储在模型的赔账 `numbers` 中，且支持多字段配置
         * Zero Extension 模块会直接根据 `X_NUMBER` 中的定义来生成相关编号，执行完成后
         * 会赋值给此处的 FSettlement 对象。
         */
        return Maker.ofST().buildFastAsync(data)


            /*
             * 根据传入的执行类型来判断当前的结算单是否完成，如果是延迟结算，则不完成
             * 如果是非延迟结算就是完成，注意直接转应收也算是结算完成，而开启了应收处理
             * 流程
             */
            .compose(generated -> {
                this.executeFinished(generated, type);
                return Ux.future(generated);
            })


            /*
             * 将构造好的 FSettlement 对象直接插入到数据库中，并且以
             * FSettlement 的方式返回上层
             */
            .compose(DB.on(FSettlementDao.class)::insertAsync);
    }

    // type -> List<FSettlement>
    @Override
    public Future<List<FSettlement>> scatter(final JsonArray data, final EmPay.Type assist) {
        /*
         * 批量结算单模式，先提取 number 的定义，直接从 data 中的 "indent" 中提取唯一
         * 的序号定义，然后执行批量生成结算单，最终返回生成的结算单列表
         */
        final String indent = Ut.valueString(data, KName.INDENT);
        return Maker.ofST().buildAsync(data, indent)
            .compose(generatedList -> {
                generatedList.forEach(generated -> this.executeFinished(generated, assist));
                return Ux.future(generatedList);
            })
            .compose(DB.on(FSettlementDao.class)::insertAsync);
    }

    // ---------------- 私有方法 -----------------

    /**
     * 不论批量还是单量，都要根据 {@link EmPay.Type} 来判断是否完成结算单
     * <pre><code>
     *     1. {@link EmPay.Type#AT} 为即时结算，所以这种情况是完成的
     *     3. {@link EmPay.Type#DEBT} 为转应收，所以这种情况是完成的
     *     2. {@link EmPay.Type#DELAY} 为延迟结算，所以这种情况是未完成的
     * </code></pre>
     *
     * @param settlement 结算单
     * @param type       结算类型
     */
    private void executeFinished(final FSettlement settlement, final EmPay.Type type) {
        if (EmPay.Type.DELAY == type) {
            settlement.setFinished(Boolean.FALSE);
        } else {
            // AT, DEBT 都是已完成的结算单
            settlement.setFinished(Boolean.TRUE);
            settlement.setFinishedAt(LocalDateTime.now());
        }
    }
}
