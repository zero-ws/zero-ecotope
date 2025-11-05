package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-10-22
 */
public abstract class DataSetBase implements DataSet {
    /**
     * 特殊解析，用来解析 dataSource 结构
     * <pre><code>
     *     {
     *         "children": {
     *             "{field1}": {
     *                 "sourceType": "TABLE",
     *                 "ds.table": "F_BILL"
     *             }
     *         }
     *     }
     * </code></pre>
     *
     * @param data      原始数据
     * @param childrenJ children 节点配置
     *
     * @return 原始数据
     */
    protected Future<JsonArray> loadChildren(final JsonArray data, final JsonObject childrenJ) {
        final JsonObject configJ = Ut.valueJObject(childrenJ);
        if (Ut.isNil(configJ)) {
            return Ut.future(data);
        }
        // children 迭代
        final ConcurrentMap<String, Future<JsonArray>> dataMap = new ConcurrentHashMap<>();
        final ConcurrentMap<String, String> outMap = new ConcurrentHashMap<>();
        Ut.<JsonObject>itJObject(childrenJ).forEach(entry -> {
            // 提取数据的子节点
            final String refField = entry.getKey();
            final JsonObject refConfig = entry.getValue();
            /*
             * 若没有 input，则提取 refIdField，若存在 input，那么值必须走 input
             */
            final String whereField = this.loadKey(refField, refConfig);

            final JsonArray ids = Ut.valueJArray(data, whereField);
            final JsonObject condition = Ux.whereAnd();
            if (refConfig.containsKey("byField")) {
                final Object byFieldValue = refConfig.getValue("byField");

                if (byFieldValue instanceof Boolean) {
                    // 如果 byField 是布尔类型
                    if ((Boolean) byFieldValue) {
                        condition.put(refField + ",i", ids);
                    } else {
                        condition.put(this.loadKey(refConfig) + ",i", ids);
                    }
                } else if (byFieldValue instanceof final String byFieldStr) {
                    // 如果 byField 是字符串类型list = {ArrayList@32413}  size = 2
                    condition.put(byFieldStr + ",i", ids);
                } else {
                    // 默认处理
                    condition.put(this.loadKey(refConfig) + ",i", ids);
                }
            } else {
                condition.put(this.loadKey(refConfig) + ",i", ids);
            }
            final DataSet dataSet = DataSet.of(refConfig);
            dataMap.put(whereField, dataSet.loadAsync(condition));
            // input / refField -> output
            outMap.put(whereField, Ut.valueString(refConfig, KName.OUTPUT));
        });
        return Fx.combineM(dataMap).compose(queryMap -> {
            /*
             * {
             *     "field": {
             *         "value": {
             *         }
             *     }
             * }
             */
            final JsonObject childData = new JsonObject();
            // 数据连接
            queryMap.forEach((refField, queryData) -> {
                // refConfig
                final JsonObject refConfig = childrenJ.getJsonObject(refField);
                final String whereField = this.loadKey(refField, refConfig);
                final String keyField = this.loadKey(refConfig);
                // 数据连接
                if (refConfig.containsKey("byField")) {
                    final Object byFieldValue = refConfig.getValue("byField");
                    if (byFieldValue instanceof Boolean) {
                        // 如果 byField 是布尔类型
                        if ((Boolean) byFieldValue) {
                            final ConcurrentMap<String, JsonObject> whereMap = Ut.elementMap(queryData, whereField);
                            // 开始连接
                            childData.put(whereField, Ut.toJObject(whereMap));
                        }
                    }
                    if (byFieldValue instanceof final String byFieldStr) {
                        final ConcurrentMap<String, JsonObject> whereMap = Ut.elementMap(queryData, byFieldStr);
                        childData.put(whereField, Ut.toJObject(whereMap));
                    }
                } else {
                    final ConcurrentMap<String, JsonObject> whereMap = Ut.elementMap(queryData, keyField);
                    // 开始连接
                    childData.put(whereField, Ut.toJObject(whereMap));
                }
            });

            Ut.itJArray(data).forEach(dataEach -> {
                // 每个字段处理
                final Set<String> fieldSet = childData.fieldNames();
                fieldSet.forEach(inputField -> {
                    final String inputValue = dataEach.getString(inputField);
                    // 原始记录是否包含 field 对应值
                    final JsonObject outData = childData.getJsonObject(inputField);
                    final JsonObject outValue = outData.getJsonObject(inputValue);
                    final String outField = outMap.get(inputField);

                    // 是否可提取对应的值
                    dataEach.put(outField, outValue);
                });
            });
            return Ux.future(data);
        });
    }

    private String loadKey(final JsonObject refConfig) {
        final String whereKey = Ut.valueString(refConfig, "where");
        return Ut.isNil(whereKey) ? KName.KEY : whereKey;
    }

    private String loadKey(final String refField, final JsonObject refConfig) {
        /*
         * 若没有 input，则提取 refIdField，若存在 input，那么值必须走 input
         */
        final String whereField;
        if (refConfig.containsKey(KName.INPUT)) {
            // input
            whereField = Ut.valueString(refConfig, KName.INPUT);
        } else {
            // ref ----
            whereField = refField;
        }
        return whereField;
    }
}
