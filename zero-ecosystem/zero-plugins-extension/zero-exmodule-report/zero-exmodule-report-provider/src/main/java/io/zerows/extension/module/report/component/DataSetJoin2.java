package io.zerows.extension.module.report.component;

import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.epoch.store.jooq.ADJ;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2024-10-22
 */
@Slf4j
class DataSetJoin2 extends DataSetBase {
    protected final MDConnect active;
    protected final MDConnect standBy;
    protected final JsonObject children = new JsonObject();
    protected final Kv<String, String> kvJoin;
    protected final JsonObject aliasJ = new JsonObject();

    DataSetJoin2(final JsonObject sourceJ) {
        final String active = Ut.valueString(sourceJ, KName.ACTIVE);
        this.active = OCacheConfiguration.entireConnect(active);
        Objects.requireNonNull(this.active);
        final String standBy = Ut.valueString(sourceJ, "standby");
        this.standBy = OCacheConfiguration.entireConnect(standBy);
        Objects.requireNonNull(this.standBy);
        this.children.mergeIn(Ut.valueJObject(sourceJ, KName.CHILDREN));

        final String activeField = Ut.valueString(sourceJ, "active.field", KName.KEY);
        final String standbyField = Ut.valueString(sourceJ, "standby.field", KName.KEY);
        this.kvJoin = Kv.create(activeField, standbyField);

        this.aliasJ.mergeIn(Ut.valueJObject(sourceJ, KName.ALIAS));
    }

    @Override
    public Future<JsonArray> loadAsync(final JsonObject params, final JsonObject queryJ) {
        // final JsonObject queryJ = Ut.toJObject(this.dataSet.getDataQuery());
        final JsonObject parameters = Tool.inputParameter(params, queryJ);
        if (Ut.isNil(parameters)) {
            return Ux.futureA();
        }
        log.info("[ ZERO ] 报表处理 / Active = {}, StandBy = {}, Cond: {}",
            this.active.getTable(), this.standBy.getTable(), parameters.encode());
        // 提取 UxJoin
        final ADJ jq = this.ofADJ();

        return jq.fetchAsync(parameters).compose(data -> this.loadChildren(data, this.children));
    }

    /**
     * 构造多表访问器
     * findAlias 的数据结构如
     * <pre><code>
     *     "findAlias": {
     *          "{TABLE1}": [
     *              field1,
     *              field2,
     *          ],
     *          "{TABLE2}": [
     *              field1,
     *              field2
     *          ]
     *     }
     * </code></pre>
     *
     * 此处针对数据结构要做一个说明，此处的数据结构
     * <pre>
     *     {
     *         "active": "X_CATEGORY",
     *         "active.field": "key",
     *         "standby": "F_PAY_TERM",
     *         "standby.field": "category",
     *         "findAlias": {
     *             "X_CATEGORY": [
     *                 "key",
     *                 "categoryId"
     *             ],
     *             "F_PAY_TERM": [
     *                 "key",
     *                 "payTermId"
     *             ]
     *         }
     *     }
     * </pre>
     * 根据上述数据结构，整体配置如下
     * <pre>
     *     1. X_CATEGORY JOIN F_PAY_TERM
     *     2. on X_CATEGORY.key = F_PAY_TERM.category
     *     3. 别名用于提取时使用
     *        findAlias -> X_CATEGORY.key -> categoryId
     *                 F_PAY_TERM.payTermId -> payTermId
     *     *: 此处非列名，全是属性名
     * </pre>
     *
     * @return 多表访问器
     */
    public ADJ ofADJ() {
        final DBNode nodeLeft = this.active.forJoin();
        final DBNode nodeRight = this.standBy.forJoin();
        final DBRef ref = DBRef.of(nodeLeft, nodeRight, this.kvJoin);
        // 别名计算和追加
        for (final String table : this.aliasJ.fieldNames()) {
            final JsonArray array = Ut.valueJArray(this.aliasJ, table);
            if (2 != array.size()) {
                log.warn("[ ZERO ] 请检查 findAlias 的配置信息：{} / {}", table, array.encode());
                continue;
            }
            final String name = array.getString(0);
            final String alias = array.getString(1);
            ref.alias(table, name, alias);
        }
        return DB.on(ref);
    }
}
