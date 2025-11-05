package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.domain.tables.daos.FBillItemDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * 步骤二：根据数据构造 BillItem 的专用接口，完整步骤：
 * <pre><code>
 *     1. 直接提取 items 节点中的数据（ data ）
 *     2. 根据 items 中的数据构造 BillItem
 *     3. 批量更新 BillItem {@link FBillItem}，这个步骤放到外层去完成，因为此处可能会出现批量模式
 * </code></pre>
 * 当前对象只负责构造 BillItem 的数据，不负责更新 BillItem 的数据，整体结构如下：
 * <pre><code>
 *     -> JsonObject
 *            items ( JsonArray ),  {@link FSettlement}
 *     -> JsonObject
 *            items ( JsonArray ),  {@link FSettlement}
 *     上述结构中如果是多条记录，但当前组件中由于 {@link JsonArray} 类型的数据中并不会出现 settlementId 的
 *     属性（第一次生成），所以只会执行单条记录的构造，而不会执行批量构造，如果是执行批量构造，会采取另外的类来
 *     实现，第一个参数不可以是单个对象，而应该是多个对象。
 * </code></pre>
 *
 * @author lang : 2024-01-19
 */
class Step02BillItem implements Step<FSettlement, FBillItem> {
    // FSettlement -> List<FBillItem>
    @Override
    public Future<List<FBillItem>> scatter(final JsonObject data, final FSettlement inserted) {
        final JsonArray items = Ut.valueJArray(data, KName.ITEMS);
        return this.buildItems(items, inserted)
            .compose(DB.on(FBillItemDao.class)::updateAsync);
    }

    // List<FSettlement> -> List<FBillItem>

    /**
     * 单独说明，批量生成结算单的时候需要执行结算单的先处理模式，必须优先将结算单的 ID 设置才可用，简单说
     * 必须对 {@link JsonArray} 执行分组，且按 `key` 分组，此处的 {@link JsonArray} 是结算单的
     * 基础数据，而不是账单子项的数据，内置会包含 `items` 存储账单明细相关信息，根据 `key` 分组之后形成
     * 的哈希表为：（结算单ID = JsonArray） 的模型，配合 {@link FSettlement} 的主键，可以直接更新
     * 结算单关联的所有账单子项。
     * <pre><code>
     *     此方法最大的限制在于结算单的 `key` 必须提前给，如果不提前给会导致插入的 `key` 和结算单中的 `key`
     *     不一致而导致无法更新，所以批量模式下的结算单主键计算是一个难点
     * </code></pre>
     *
     * @param data     传入的数据
     * @param inserted 已经插入的结算单
     *
     * @return {@link Future}
     */
    @Override
    public Future<List<FBillItem>> flatter(final JsonArray data, final List<FSettlement> inserted) {
        final ConcurrentMap<String, JsonArray> grouped = Ut.elementGroup(data, KName.KEY);
        final ConcurrentMap<String, FSettlement> mapped = Ut.elementMap(inserted, FSettlement::getKey);

        final List<Future<List<FBillItem>>> futures = new ArrayList<>();
        mapped.forEach((key, settlement) -> {
            final JsonArray dataArray = grouped.get(key);
            futures.add(this.buildItems(dataArray, settlement));
        });
        return FnBase.combineT(futures).compose(combined -> {
            final List<FBillItem> items = new ArrayList<>();
            combined.forEach(items::addAll);
            return Ux.future(items);
        }).compose(DB.on(FBillItemDao.class)::updateAsync);
    }

    private Future<List<FBillItem>> buildItems(final JsonArray data, final FSettlement inserted) {
        /*
         * 结算单创建之后，要将 BillItem 账单子项中的 settlementId 和状态执行更新
         * Bill              Settlement
         *    \                /
         *     \              /
         *      \            /
         *        BillItem ( billId, settlementId )
         *
         * 此处的数据结构：
         * {
         *     "items": []
         * }
         * 此处的 items 的类型就是 JsonArray 类型，包含了所有的 BillItem （当前结算的）
         * 且此处需要说明一点，就是关于 FBillItem 的状态，这里的逻辑是账单子项完成结算，所以
         * 所有的账单子项状态都会是 Finished，简单说流程走到这里账单子项以及账单本身就已经完
         * 成了。
         */
        Objects.requireNonNull(inserted);
        final JsonArray dataArray = Ut.valueJArray(data);
        return Maker.ofBI().buildAsync(dataArray, inserted)
            .compose(items -> {
                /*
                 * 结合已经创建好的结算单，计算结算单和账单子项的关系，此处更新的内容如：
                 * - settlementId：账单子项中结算单主键
                 * - updatedAt / updatedBy：更新人、更新时间（Auditor相关信息）
                 */
                IkWay.ofST2BI().transfer(inserted, items);
                /*
                 * 为了保证并发不出错的问题，此处不做 updateAsync，这个过程放到外层去做
                 * 如果此处这样做会引起不同的 Settlement 关联的 BillItem 的更新出现并发
                 * 的问题，所以此处需要调整，将整个操作放到外层去处理，才能保证并发不出错
                 */
                return Ux.future(items); // Ux.Jooq.join(FBillItemDao.class).updateAsync(items);
            });
    }
}
