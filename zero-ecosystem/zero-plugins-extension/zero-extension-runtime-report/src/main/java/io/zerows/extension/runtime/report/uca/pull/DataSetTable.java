package io.zerows.extension.runtime.report.uca.pull;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.extension.HExtension;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpDataSet;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 最常见的数据源读取器，直接读取表格数据，也可以作为其他类的基类，其他模式直接从此处继承
 * <pre><code>
 *     内置包含 {@link KpDataSet} 的引用，用于处理报表的数据加载
 * </code></pre>
 *
 * @author lang : 2024-10-12
 */
class DataSetTable extends AbstractDataSet {
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
        this.connect = HExtension.connect(tableName);
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

        this.logger().info("Report processing for table = {}, Cond: {}",
            this.connect.getTable(), parameters.encode());
        // 提取 UxJooq
        final UxJooq jq = Ux.Jooq.bridge(this.connect);
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
