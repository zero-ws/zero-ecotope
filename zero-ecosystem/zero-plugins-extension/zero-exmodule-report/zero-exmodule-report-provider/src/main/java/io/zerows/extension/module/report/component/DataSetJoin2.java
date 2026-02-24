package io.zerows.extension.module.report.component;

import io.r2mo.base.dbe.Join;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.epoch.store.DBSActor;
import io.zerows.epoch.store.jooq.ADJ;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.epoch.web.MDConnect;
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
    private static final String DEFAULT_YML = "pojo/{0}.yml";

    DataSetJoin2(final JsonObject sourceJ) {
        final String active = Ut.valueString(sourceJ, KName.ACTIVE);
        this.active = OCacheConfiguration.entireConnect(active);
        Objects.requireNonNull(this.active);
        final String standby = Ut.valueString(sourceJ, "standby");
        this.standBy = OCacheConfiguration.entireConnect(standby);
        Objects.requireNonNull(this.standBy);
        this.children.mergeIn(Ut.valueJObject(sourceJ, KName.CHILDREN));

        final String activeField = Ut.valueString(sourceJ, "active.field", KName.ID);
        final String standbyField = Ut.valueString(sourceJ, "standby.field", KName.ID);
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
     * <pre>
     * 配置示例：
     * {
     *     "active": "X_CATEGORY",
     *     "active.field": "key",
     *     "standby": "F_PAY_TERM",
     *     "standby.field": "category",
     *     "alias": {
     *         "X_CATEGORY": ["key", "categoryId"],
     *         "F_PAY_TERM": ["key", "payTermId"]
     *     }
     * }
     *
     * 说明：
     * 1. X_CATEGORY JOIN F_PAY_TERM ON X_CATEGORY.key = F_PAY_TERM.category
     * 2. alias 用于字段别名映射：X_CATEGORY.key -> categoryId
     * </pre>
     */
    public ADJ ofADJ() {
        final Join join = Join.of(
            this.active.meta().dao(),
            this.kvJoin.key(),
            this.standBy.meta().dao(),
            this.kvJoin.value()
        );

        final Kv<String, String> vectorPojo = Kv.create(
            this.active.getPojoFile(),
            this.standBy.getPojoFile()
        );

        final ADJ adj = DB.on(join, vectorPojo, DBSActor.ofDBS());

        if (!this.aliasJ.isEmpty()) {
            log.info("[ ZERO ] 设置别名，配置：{}", this.aliasJ.encode());
            this.aliasJ.fieldNames().forEach(table -> this.applyAlias(adj, table));
        }

        return adj;
    }

    private void applyAlias(final ADJ adj, final String table) {
        final JsonArray array = Ut.valueJArray(this.aliasJ, table);
        if (array.size() != 2) {
            log.warn("[ ZERO ] alias 配置错误：{} / {}", table, array.encode());
            return;
        }

        final Class<?> daoClass = this.resolveDaoClass(table);
        if (daoClass == null) {
            log.warn("[ ZERO ] 未知的表名：{}", table);
            return;
        }

        final String name = array.getString(0);
        final String alias = array.getString(1);

        try {
            adj.alias(daoClass, name, alias);
            log.info("[ ZERO ] 别名设置成功 / {} -> {}.{}", alias, table, name);
        } catch (Exception ex) {
            log.warn("[ ZERO ] 别名设置失败：{}.{} -> {}", table, name, alias);
        }
    }

    private Class<?> resolveDaoClass(final String table) {
        if (table.equals(this.active.getTable())) {
            return this.active.meta().dao();
        }
        if (table.equals(this.standBy.getTable())) {
            return this.standBy.meta().dao();
        }
        return null;
    }
}
