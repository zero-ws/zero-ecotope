package io.zerows.extension.runtime.report.uca.pull;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.exception.web._60050Exception501NotSupport;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpDataSet;
import io.zerows.extension.runtime.report.eon.RpConstant;
import io.zerows.extension.runtime.report.eon.em.EmReport;
import io.zerows.extension.runtime.report.uca.feature.RQueryComponent;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

import java.util.Objects;

/**
 * 数据源加载器，用于处理 dataSource 字段定义的数据源的相关信息加载，主要依赖
 * <pre><code>
 *     - dataSource 配置
 *     - dataQuery 配置
 * </code></pre>
 * 为报表专用处理器，用于处理数据源的加载
 *
 * @author lang : 2024-10-12
 */
public interface DataSet {

    Cc<String, DataSet> CC_SKELETON = Cc.openThread();
    Cc<String, RQueryComponent> CC_OUT = Cc.openThread();

    static DataSet of(final JsonObject sourceJ) {
        final EmReport.SourceType type = Ut.toEnum(
            () -> Ut.valueString(sourceJ, "sourceType"), EmReport.SourceType.class);
        final String dsTarget;
        // TABLE
        if (EmReport.SourceType.TABLE == type) {
            dsTarget = Ut.valueString(sourceJ, RpConstant.SourceTypeField.TABLE);
            final JsonObject paramConstructor = new JsonObject();

            paramConstructor.put(KName.SOURCE, dsTarget);
            paramConstructor.put(KName.CHILDREN, sourceJ.getValue(KName.CHILDREN));
            return CC_SKELETON.pick(
                () -> new DataSetTable(paramConstructor), type + VString.SLASH + dsTarget
            );
        }
        // JOIN_2
        if (EmReport.SourceType.JOIN_2 == type) {
            final JsonObject paramConstructor = Ut.valueJObject(sourceJ, RpConstant.SourceTypeField.SOURCE);
            paramConstructor.put(KName.CHILDREN, sourceJ.getValue(KName.CHILDREN));
            return CC_SKELETON.pick(
                () -> new DataSetJoin2(paramConstructor), type + VString.SLASH + paramConstructor.hashCode()
            );
        }
        // Not Support
        throw new _60050Exception501NotSupport(DataSet.class);
    }

    /**
     * 直接根据报表定义读取相关配置
     *
     * @param params 读取参数
     * @param queryJ 查询配置
     *
     * @return 返回读取的数据
     */
    Future<JsonArray> loadAsync(JsonObject params, JsonObject queryJ);

    default Future<JsonArray> loadAsync(final JsonObject params) {
        return this.loadAsync(params, null);
    }

    default OLog logger() {
        return Ut.Log.database(this.getClass());
    }

    interface Tool {

        static JsonObject inputParameter(final JsonObject params, final JsonObject queryJ) {
            if (Ut.isNil(queryJ)) {
                return params;
            } else {
                final JsonObject conditionTpl = Ut.valueJObject(queryJ, "condition");
                return Ut.fromExpression(conditionTpl, params);
            }
        }

        static Future<JsonArray> outputArray(final JsonObject params, final KpDataSet dataSet) {
            /**
             * 先分流 不能二义性
             *
             */
            final JsonObject sourceJ = Ut.toJObject(dataSet.getDataSource());
            final DataSet executor = DataSet.of(sourceJ);
            final JsonObject queryDef = Ut.toJObject(dataSet.getDataQuery());

            if (dataSet.getDataComponent() != null) {
                return executor.loadAsync(params, queryDef).compose(dataSouce -> {
                    final String dataComponent = dataSet.getDataComponent();
                    final RQueryComponent queryComponent = DataSet.CC_OUT.pick(() -> Ut.instance(dataComponent), dataComponent);
                    final JsonObject parameters = new JsonObject();
                    parameters.put(KName.INPUT, params);
                    return queryComponent.dataAsync(dataSouce, parameters).compose(result -> {
                        if (Objects.isNull(result)) {
                            return Ut.future(dataSouce);
                        }
                        return Ut.future(result);
                    });
                });
            }
            return executor.loadAsync(params, queryDef);
        }
    }
}
