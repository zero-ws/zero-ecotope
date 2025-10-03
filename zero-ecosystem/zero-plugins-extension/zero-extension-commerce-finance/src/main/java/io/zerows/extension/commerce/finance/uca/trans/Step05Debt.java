package io.zerows.extension.commerce.finance.uca.trans;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.metadata.program.KRef;
import io.zerows.constant.VValue;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.finance.domain.tables.daos.FDebtDao;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.commerce.finance.uca.enter.Maker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 特殊接口，直接使用 {@link FSettlement} 的集合创建 应收/应退 的单据，此处设置的点：
 * <pre><code>
 *     根据 {@link FSettlement} 提取集合，并根据此集合构造单个 应收/应退 的单据，应收处理
 *     过程中有可能会跨越多个结算单来执行，所以此处的数据结构是：{@link List} 类型，每个元素
 *     类型是 {@link FSettlement}
 *     应收和退款是后台生成，所以此处必须有一个常来来处理序号定义相关信息，这个常量就是：
 *     {@link FmConstant.NUM#DEBT} 和 {@link FmConstant.NUM#REFUND}
 * </code></pre>
 * 特殊请求格式
 * <pre><code>
 *     {
 *         "...": "...",
 *         "selected": [
 *
 *         ]
 *     }
 *     "selected" 在此处表示在选择模式下提取  {@link FSettlementItem} 集合的主键值，这种模式下执行
 *     1. 先根据 {@link FSettlement} 提取所有合法的结算明细
 *     2. 再根据提取的所有结算明细执行 selected 过滤得到最终的结算明细
 * </code></pre>
 *
 * @author lang : 2024-01-22
 */
class Step05Debt implements Trade<List<FSettlement>, FDebt> {
    @Override
    public Future<FDebt> flatter(final JsonObject data, final List<FSettlement> settlements) {
        /*
         * 先构造应收 / 退款单据
         * 新版这种类型的单据的单号会有变化，原始
         * - R{结算单号} - 退款单号
         * - D{结算单号} - 应收单号
         *
         * 新版直接根据 X_NUMBER 中的定义，因为此处新版已经去掉了原始的单号生成逻辑，而且改成了可以跨
         * 结算单处理的模式，所以此处无法再直接和单个结算单绑定到一起，序号生成会有很大的变化。
         * - NUM.DEBT - 应收单号
         * - NUM.REFUND - 退款单号
         * 应收和退款取决于金额之和的最终结果，如果是正数则是应收，如果是负数则是退款。
         */
        final Set<String> keySelected = Ut.toSet(Ut.valueJArray(data, "selected"));

        final KRef ref = new KRef();

        return this.executeItems(settlements, keySelected)
            .compose(ref::future)
            .compose(items -> Maker.ofD().buildAsync(data, items))
            .compose(entity -> {
                final String string = data.getString("amount");
                entity.setAmount(new BigDecimal(string));
                entity.setAmountBalance(new BigDecimal(string));
                entity.setKey(null);
                return Ux.Jooq.on(FDebtDao.class).insertAsync(entity);
            })
            .compose(inserted -> {
                // 更新 items
                final List<FSettlementItem> items = ref.get();
                items.forEach(item -> item.setDebtId(inserted.getKey()));
                return Ux.Jooq.on(FSettlementItemDao.class).updateAsync(items)
                    .compose(nil -> Ux.future(inserted));
            });
    }

    private Future<List<FSettlementItem>> executeItems(
        final List<FSettlement> settlements, final Set<String> keySelected) {
        if (settlements.isEmpty()) {
            return Ux.futureL();
        }
        final Set<String> ids = settlements.stream()
            .map(FSettlement::getKey)
            .collect(Collectors.toSet());

        final String sigma = settlements.get(VValue.IDX).getSigma();
        // 读取所有的结算单明细
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.SIGMA, sigma);
        condition.put(KName.Finance.SETTLEMENT_ID + ",i", Ut.toJArray(ids));
        return Ux.Jooq.on(FSettlementItemDao.class).<FSettlementItem>fetchAndAsync(condition).compose(items -> {
            // 如果没有选择模式，则直接返回所有的结算单明细，否则返回选择的结算单明细
            if (Objects.isNull(keySelected) || keySelected.isEmpty()) {
                return Ux.future(items);
            }
            return Ux.future(items.stream()
                .filter(item -> keySelected.contains(item.getKey()))
                .collect(Collectors.toList()));
        });
    }
}
