package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.report.domain.tables.pojos.KpDataSet;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 最常见的数据源读取器，直接读取表格数据，也可以作为其他类的基类，其他模式直接从此处继承
 * <pre><code>
 *     内置包含 {@link KpDataSet} 的引用，用于处理报表的数据加载
 * </code></pre>
 *
 * @author lang : 2024-10-12
 */
@Slf4j
class DataSetTable extends DataSetBase {
    protected final MDConnect connect;
    protected final JsonObject children = new JsonObject();

    /**
     * <pre><code>
     * {
     *     "source": "",
     *     "children": {
     *
     *     }
     * }
     * </code></pre>
     *
     * @param sourceJ 配置
     */
    DataSetTable(final JsonObject sourceJ) {
        final String tableName = Ut.valueString(sourceJ, KName.SOURCE);
        this.connect = OCacheConfiguration.entireConnect(tableName);
        Objects.requireNonNull(this.connect);
        this.children.mergeIn(Ut.valueJObject(sourceJ, KName.CHILDREN));
    }

    @Override
    public Future<JsonArray> loadAsync(final JsonObject params, final JsonObject queryJ) {
        // final JsonObject queryJ = Ut.toJObject(this.dataSet.getDataQuery());
        final JsonObject parameters = Tool.inputParameter(params, queryJ);
        if (Ut.isNil(parameters)) {
            return Ux.futureA();
        }

        log.info("[ XMOD ] ( RPT ) 报表执行结果：table = {} / 条件 = {}",
            this.connect.getTable(), parameters.encode());
        // 提取 UxJooq
        final ADB jq = DB.on(this.connect);
        if (parameters.getBoolean("") != null) {
            if (!parameters.getBoolean("")) {
                return jq.fetchJOrAsync(parameters).compose(data -> this.loadChildren(data, this.children));
            } else {
                return jq.fetchJAndAsync(parameters).compose(data -> this.loadChildren(data, this.children));
            }
        } else {
            return jq.fetchJAndAsync(parameters).compose(data -> this.loadChildren(data, this.children));

        }
    }
}
