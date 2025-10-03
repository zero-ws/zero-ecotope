package io.zerows.extension.runtime.report.uca.combiner;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReport;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;
import io.zerows.extension.runtime.report.eon.RpConstant;
import io.zerows.extension.runtime.report.eon.em.EmReport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yu : 2024-12-31
 */
public class StepGeneratorTotal extends AbstractStepGenerator {
    StepGeneratorTotal(final RGeneration generation) {
        super(generation);
    }

    @Override
    public Future<KpReportInstance> build(final KpReportInstance instance, final JsonObject request, final JsonArray sourceData) {
        final KpReport report = this.metadata().reportMeta();
        final RGeneration metadata = this.metadata();
        final List<KpFeature> features = metadata.featureData();
        final JsonObject reportConfig = Ut.toJObject(report.getReportConfig());
        final JsonObject bottomTotal = Ut.valueJObject(reportConfig, RpConstant.ConfigField.TOTAL);
        final JsonObject totalCount = Ut.valueJObject(reportConfig, RpConstant.ConfigField.TOTAL_COUNT);
        final JsonObject reportContent = new JsonObject(instance.getReportContent());
        final JsonArray data = reportContent.getJsonArray(KName.DATA);
        final JsonArray children = new JsonArray();
        data.forEach(item -> {
            final JsonObject entries = Ux.toJson(item);
            final JsonArray jsonArray = entries.getJsonArray(KName.CHILDREN);
            jsonArray.forEach(children::add);
        });
        final ConcurrentHashMap<String, String> total = new ConcurrentHashMap<>();
        if (!bottomTotal.fieldNames().isEmpty()) {
            bottomTotal.fieldNames().forEach(dimFeature -> {
                // 提取 Feature
                final KpFeature feature = Ut.elementFind(features, item -> item.getName().equals(dimFeature));
                final EmReport.FeatureType featureType = Ut.toEnum(feature.getType(), EmReport.FeatureType.class, EmReport.FeatureType.NONE);
                if (EmReport.FeatureType.AGGR == featureType) {
                    children.forEach(item -> {
                        final JsonObject entries = Ux.toJson(item);
                        final String string = entries.getString(feature.getName());
                        // 使用 BigDecimal.valueOf 保留两位小数
                        final BigDecimal value = (string == null || string.isEmpty()) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(string)).setScale(2, RoundingMode.HALF_UP);
                        // 转换为字符串，确保两位小数
                        final String valueString = value.toString();
                        // 使用 compute 方法累加值
                        total.compute(feature.getName(), (key, current) -> {
                            if (current == null) {
                                return valueString; // 如果该键没有值，直接使用当前值
                            } else {
                                // 将当前值和新值转换为 BigDecimal，进行累加
                                final BigDecimal currentValue = new BigDecimal(current);
                                final BigDecimal newValue = new BigDecimal(valueString);
                                final BigDecimal sum = currentValue.add(newValue).setScale(2, RoundingMode.HALF_UP);
                                return sum.toString(); // 返回累加后的值
                            }
                        });
                    });
                }
            });
        }

        if (!totalCount.fieldNames().isEmpty()) {
            totalCount.fieldNames().forEach(count -> {
                final String formula = totalCount.getString(count);
                Object result = Ut.fromExpressionT(formula, Ux.toJson(total));
                if (result == null) {
                    result = "0.00";
                }
                final BigDecimal bigDecimal = new BigDecimal(result.toString());
                final BigDecimal truncatedValue = bigDecimal.setScale(2, RoundingMode.DOWN);
                total.put(count, truncatedValue.toString());
            });
        }
        if (!total.keySet().isEmpty()) {
            final JsonObject entries = Ux.toJson(total);
            entries.put(KName.KEY, UUID.randomUUID().toString());
            bottomTotal.fieldNames().forEach(item -> {
                final boolean b = total.containsKey(item);
                if (!b) {
                    entries.put(item, bottomTotal.getString(item));
                }
            });
            final JsonObject entries1 = Ux.cloneT(entries);
            entries.put(KName.CHILDREN, new JsonArray().add(entries1));
            data.add(entries);
        }
        reportContent.put(KName.DATA, data);
        instance.setReportContent(reportContent.toString());
        return Ux.future(instance);
    }
}
