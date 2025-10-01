package io.zerows.extension.runtime.report.atom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VValue;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReport;
import io.zerows.extension.runtime.report.eon.em.EmReport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-04
 */
public class RGeneration implements Serializable {

    private final ConcurrentMap<String, RDimension> reportDimensions = new ConcurrentHashMap<>();
    private final List<KpFeature> reportFeatures = new ArrayList<>();
    private final ConcurrentMap<String, KpFeature> reportGlobal = new ConcurrentHashMap<>();
    private final String key;
    private KpReport reportDefinition;

    public RGeneration() {
        this.key = UUID.randomUUID().toString();
    }

    public String key() {
        return this.key;
    }

    public Future<KpReport> reportMeta(final KpReport reportDefinition) {
        this.reportDefinition = reportDefinition;
        return Future.succeededFuture(this.reportDefinition);
    }

    public Future<ConcurrentMap<String, RDimension>> dimension(final ConcurrentMap<String, RDimension> reportDimensions) {
        this.reportDimensions.clear();
        this.reportDimensions.putAll(reportDimensions);
        return Future.succeededFuture(this.reportDimensions);
    }

    public Future<List<KpFeature>> featureData(final List<KpFeature> reportFeatures) {
        this.reportFeatures.clear();
        this.reportFeatures.addAll(this.featureCompress(reportFeatures, EmReport.FeatureType.valueOk()));
        return Future.succeededFuture(this.reportFeatures);
    }

    public Future<ConcurrentMap<String, KpFeature>> featureGlobal(final ConcurrentMap<String, KpFeature> reportGlobal) {
        this.reportGlobal.clear();
        this.reportGlobal.putAll(reportGlobal);
        return Future.succeededFuture(this.reportGlobal);
    }

    // ------------- 上边方法是设置专用方法 ----------------

    /**
     * 获取全局特征
     *
     * @param key 特征名
     *
     * @return 返回特征
     */
    public KpFeature featureGlobal(final String key) {
        return this.reportGlobal.getOrDefault(key, null);
    }

    /**
     * 获取报表原始数据（定义部分）
     *
     * @return 返回报表定义
     */
    public KpReport reportMeta() {
        return this.reportDefinition;
    }

    @Deprecated
    public Set<String> featureGlobalKeys() {
        return this.reportGlobal.keySet();
    }

    public RDimension dimension() {
        if (VValue.ONE == this.reportDimensions.size()) {
            return this.reportDimensions.values().iterator().next();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<KpFeature> featureData() {
        final JsonObject reportConfig = Ut.toJObject(this.reportDefinition.getReportConfig());
        final JsonArray features = Ut.valueJArray(reportConfig, "feature");
        return this.featureCompress(this.reportFeatures, (List<String>) features.getList());
    }

    @SuppressWarnings("unchecked")
    public List<KpFeature> featureDetail() {
        final JsonObject reportConfig = Ut.toJObject(this.reportDefinition.getReportConfig());
        final JsonArray features = Ut.valueJArray(reportConfig, "feature");
        return this.featureCompress(this.featureCompress(this.reportFeatures,
            EmReport.FeatureType.LAZY, EmReport.FeatureType.DATA, EmReport.FeatureType.AGGR
        ), (List<String>) features.getList());
    }

    public List<KpFeature> featureDim() {
        return this.featureCompress(this.featureData(), EmReport.FeatureType.DIMENSION);
    }

    private List<KpFeature> featureCompress(final List<KpFeature> features,
                                            final List<String> featureNames) {
        final ConcurrentMap<String, KpFeature> featureMap = Ut.elementMap(features, KpFeature::getName);
        final List<KpFeature> featureList = new ArrayList<>();
        featureNames.forEach(featureName -> {
            if (featureMap.containsKey(featureName)) {
                featureList.add(featureMap.get(featureName));
            }
        });
        return featureList;
    }

    private List<KpFeature> featureCompress(final List<KpFeature> features,
                                            final EmReport.FeatureType... featureArr) {
        return features.stream().filter(item -> {
            final EmReport.FeatureType type =
                Ut.toEnum(item::getType, EmReport.FeatureType.class, EmReport.FeatureType.NONE);
            return Set.of(featureArr).contains(type);
        }).toList();
    }
}
