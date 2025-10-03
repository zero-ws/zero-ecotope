package io.zerows.extension.runtime.report.uca.combiner;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.report.atom.RDimension;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReport;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;
import io.zerows.extension.runtime.report.eon.RpConstant;
import io.zerows.extension.runtime.report.eon.em.EmReport;
import io.zerows.extension.runtime.report.uca.feature.OFeature;
import io.zerows.platform.constant.VValue;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-26
 */
class StepGeneratorData extends AbstractStepGenerator {

    StepGeneratorData(final RGeneration generation) {
        super(generation);
    }

    /**
     * <pre><code>
     *     reportData
     *     reportContent，数据结构如下
     *     {
     *         "feature": [
     *             {
     *                 "dataIndex": "feature01 / dataIndex",
     *                 "title": "feature01Title / title"
     *             }
     *         ],
     *         "data": [
     *             {
     *                 "feature01": "xxx",
     *                 "feature02": "xxx"
     *             }
     *         ]
     *     }
     * </code></pre>
     *
     * @param instance   新报表实例
     * @param request    请求数据
     * @param sourceData 源数据
     *
     * @return 处理后的报表实例
     */
    @Override
    public Future<KpReportInstance> build(final KpReportInstance instance, final JsonObject request, final JsonArray sourceData) {
        // reportData
        instance.setReportData(sourceData.encode());
        /*
         * 计算 reportContent
         */
        return this.calculateContent(sourceData, request).compose(dataArray -> {
            final JsonObject reportContent = new JsonObject();
            final List<KpFeature> features = this.metadata().featureData();
            final JsonArray featureA = new JsonArray();
            features.forEach(feature -> {
                final JsonObject featureItem = new JsonObject();
                final String valueConfig = feature.getValueConfig();
                if (valueConfig != null) {
                    final JsonObject entries = new JsonObject(valueConfig);
                    if (entries.getJsonObject("css") != null) {
                        featureItem.mergeIn(entries.getJsonObject("css"));
                    }
                }

                featureItem.put("dataIndex", feature.getName());
                featureItem.put("title", feature.getValueDisplay());
                featureA.add(featureItem);
            });
            reportContent.put(KName.DATA, dataArray);
            reportContent.put("feature", featureA);

            final KpFeature featureOfDim = this.metadata().featureDim().get(VValue.ZERO);
            reportContent.put("dimension", featureOfDim.getName());

            instance.setReportContent(reportContent.encode());

            return Ux.future(instance);
        });
    }

    private Future<JsonArray> calculateContent(final JsonArray sourceData, final JsonObject params) {
        final RGeneration generation = this.metadata();
        final List<KpFeature> featureDim = generation.featureDim();
        if (VValue.ONE < featureDim.size()) {
            // TODO: 多维度计算
            this.logger().warn("Current Version Does not support. key = {}", generation.key());
            return Ux.futureA();

        }
        /*
         * 单个维度特征才执行此操作
         *  - 此处的 dimension 是已经计算过的维度数据信息
         */
        final RDimension dimension = generation.dimension();
        final KpFeature featureOfDim = featureDim.get(VValue.ZERO);
        if (Objects.isNull(dimension) || Objects.isNull(featureOfDim)) {
            this.logger().warn("The dimension definition is null. key = {}", generation.key());
            return Ux.futureA();
        }

        final List<KpFeature> featureDetail = generation.featureDetail();
        // 特征数据处理 feature
        final List<KpFeature> features = generation.featureData();
        /*
         * 计算 DATA 类型的特征哈希表，这种类型位于明细上，必须执行计算，特别针对读取类型的必须计算，预处理一轮，直接调用
         * OFeature 接口中的方法，计算不同的特征值，此处比较特殊的特征就是 LAZY 类型
         *
         * 此处最终得到一个 HASH MAP
         * LAZY -> ( field = result ) /
         */
        final ConcurrentMap<String, Future<ConcurrentMap<String, Object>>> preFeatureMap = new ConcurrentHashMap<>();
        featureDetail.forEach(feature -> {
            final OFeature oFeature = OFeature.of(feature.getValuePath());
            preFeatureMap.put(feature.getName(), oFeature.outAsync(sourceData, params, feature));
        });
        return Fx.combineM(preFeatureMap).compose(matrixMap -> {
            // 抽取维度配置
            final JsonObject dimConfig = Ut.toJObject(featureOfDim.getValueConfig());
            final String dimField = Ut.valueString(dimConfig, RpConstant.DimValue.FIELD_GROUP);

            // 先根据特征处理重新提取数据，保留维度字段
            final JsonArray dataProcessed = new JsonArray();
            Ut.itJArray(sourceData).forEach(item -> {
                /*
                 * - dimKey
                 * - dimDisplay
                 * - key
                 */
                final JsonObject dataRecord = new JsonObject();
                final String recordKey = Ut.valueString(item, KName.KEY);
                dataRecord.put(KName.KEY, recordKey);      // 记录 key

                final String dimKey = Ut.valueString(item, dimField);           // 维度键
                final JsonObject dimData = dimension.data(dimKey);              // 维度数据
                dataRecord.put(RpConstant.DimField.KEY, Ut.valueString(dimData, RpConstant.DimField.KEY));   // 维度键
                final String dimValue = Ut.valueString(dimData, RpConstant.DimField.DISPLAY);
                dataRecord.put(RpConstant.DimField.DISPLAY, dimValue);                                       // 维度显示

                features.forEach(feature -> {
                    // 提取呈现结果
                    final ConcurrentMap<String, Object> featureMap = matrixMap.getOrDefault(feature.getName(), new ConcurrentHashMap<>());
                    // 特征名称填充
                    final Object value = featureMap.getOrDefault(recordKey, null);
                    if (feature.getKey().equals(featureOfDim.getKey())) {
                        // 维度字段，一定不会出现在 featureMap 中
                        dataRecord.put(feature.getName(), dimValue);
                    } else {
                        // 非维度字段
                        dataRecord.put(feature.getName(), value);
                    }
                });

                dataRecord.put(KName.__.DATA, item.copy());
                dataProcessed.add(dataRecord);
            });


            // 构造最终报表形态，先按维度分组
            final ConcurrentMap<String, JsonArray> groupMap = Ut.elementGroup(dataProcessed, RpConstant.DimField.KEY);
            final Set<String> dimKeys = dimension.dateKeys();

            // combine 节点，追加维度行
            final KpReport report = this.metadata().reportMeta();
            final JsonObject reportConfig = Ut.toJObject(report.getReportConfig());
            final JsonObject combine = Ut.valueJObject(reportConfig, RpConstant.ConfigField.COMBINE);
            // 重新构造数据记录
            final JsonArray reportData = new JsonArray();
            dimKeys.stream().filter(groupMap::containsKey).forEach(dimKey -> {
                final JsonObject dimRecord = new JsonObject();
                final JsonArray dimSource = groupMap.get(dimKey);

                /*
                 * key / dimKey
                 * dim feature
                 */
                dimRecord.put(KName.KEY, dimKey);
                final String dimValue = Ut.valueString(dimSource, RpConstant.DimField.DISPLAY);
                dimRecord.put(featureOfDim.getName(), dimValue);

                /*
                 * 1. fixed configured ( reportConfig )
                 * 2. dynamic calculated ( Feature )
                 */
                combine.fieldNames().forEach(dimFeature -> {
                    // 提取 Feature
                    final KpFeature feature = Ut.elementFind(features, item -> item.getName().equals(dimFeature));
                    if (Objects.nonNull(feature)) {
                        // 查看类型
                        final EmReport.FeatureType featureType = Ut.toEnum(feature.getType(), EmReport.FeatureType.class, EmReport.FeatureType.NONE);
                        if (EmReport.FeatureType.AGGR == featureType) {
                            // 聚集，启用聚集计算
                            final Object aggrValue = dimension.runRule(feature.getName(), dimSource);
                            dimRecord.put(feature.getName(), aggrValue);
                        } else {
                            // 固定值
                            final Object fixedValue = combine.getValue(dimFeature);
                            dimRecord.put(feature.getName(), fixedValue);
                        }
                    }
                });
                dimRecord.put(KName.CHILDREN, dimSource);
                reportData.add(dimRecord);
            });
            return Ux.future(reportData);
        });
    }
}
