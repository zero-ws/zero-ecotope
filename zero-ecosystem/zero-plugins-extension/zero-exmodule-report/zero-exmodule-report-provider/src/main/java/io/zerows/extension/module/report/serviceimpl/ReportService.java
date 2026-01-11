package io.zerows.extension.module.report.serviceimpl;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.report.common.RDimension;
import io.zerows.extension.module.report.common.RGeneration;
import io.zerows.extension.module.report.common.em.EmReport;
import io.zerows.extension.module.report.component.DataSet;
import io.zerows.extension.module.report.component.DimProc;
import io.zerows.extension.module.report.domain.tables.daos.KpDataSetDao;
import io.zerows.extension.module.report.domain.tables.daos.KpDimensionDao;
import io.zerows.extension.module.report.domain.tables.daos.KpFeatureDao;
import io.zerows.extension.module.report.domain.tables.daos.KpReportDao;
import io.zerows.extension.module.report.domain.tables.pojos.KpDataSet;
import io.zerows.extension.module.report.domain.tables.pojos.KpDimension;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.module.report.domain.tables.pojos.KpReport;
import io.zerows.extension.module.report.exception._80701Exception404ReportMissing;
import io.zerows.extension.module.report.exception._80702Exception404ReportDataSet;
import io.zerows.extension.module.report.servicespec.ReportInstanceStub;
import io.zerows.extension.module.report.servicespec.ReportStub;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-10-08
 */
public class ReportService implements ReportStub {
    @Inject
    private ReportInstanceStub instanceStub;

    public void setInstanceStub(final ReportInstanceStub instanceStub) {
        this.instanceStub = instanceStub;
    }

    @Override
    public Future<JsonArray> fetchReports(final String appId) {
        final JsonObject qr = Ux.whereAnd();
        qr.put(KName.APP_ID, appId);
        qr.put(KName.ACTIVE, Boolean.TRUE);
        return DB.on(KpReportDao.class).fetchJAndAsync(qr);
    }

    @Override
    public Future<JsonObject> buildInstance(final String reportId, final JsonObject params) {
        return DB.on(KpReportDao.class).<KpReport>fetchByIdAsync(reportId)
            .compose(report -> {
                if (Objects.isNull(report)) {
                    // ERR-80701
                    return FnVertx.failOut(_80701Exception404ReportMissing.class, reportId);
                }
                // 配置构造
                return this.buildGeneration(report, params);
            })
            .compose(generation -> {
                // 维度数据源
                final KpReport report = generation.reportMeta();
                return this.buildData(report, params)
                    // 实例生成
                    .compose(data -> this.instanceStub.buildAsync(data, params, generation));
            })
            .compose(Ux::futureJ)
            .map(item -> Ut.valueToJObject(item, "reportData", "reportContent"));
    }

    /**
     * 读取主数据源
     *
     * @param report 报表定义
     * @param params 参数
     * @return 数据源
     */
    @Override
    public Future<JsonArray> buildData(final KpReport report, final JsonObject params) {
        final String reportId = report.getKey();
        final String dsId = report.getDataSetId();
        if (Ut.isNil(dsId)) {
            // ERR-80702
            return FnVertx.failOut(_80702Exception404ReportDataSet.class, reportId);
        }
        return DB.on(KpDataSetDao.class).<KpDataSet>fetchByIdAsync(dsId).compose(dataSet -> {
            if (Objects.isNull(dataSet)) {
                // ERR-80702
                return FnVertx.failOut(_80702Exception404ReportDataSet.class, reportId);
            }

            return DataSet.Tool.outputArray(params, dataSet);
        });
    }


    // -------------- 维度部分 ---------------
    @Override
    public Future<RGeneration> buildGeneration(final KpReport report, final JsonObject params) {
        final RGeneration refGen = new RGeneration();
        final KRef futureList = new KRef();
        // 主报表
        return Ux.future(report).compose(refGen::reportMeta)
            // 维度处理
            .compose(processed -> this.reportOfDim(processed, params)).compose(refGen::dimension)
            // 全特征提取
            .compose(dimensions -> this.featureOfAll(report)).compose(futureList::future)
            // 属性特征
            .compose(nil -> this.featureOfDim(report, futureList.get()).compose(refGen::featureData))
            // 全局特征
            .compose(nil -> this.featureOfGlobal(futureList.get())).compose(refGen::featureGlobal)
            // 返回结果
            .compose(nil -> Ux.future(refGen));
    }


    private Future<ConcurrentMap<String, KpFeature>> featureOfGlobal(final List<KpFeature> featureList) {
        final ConcurrentMap<String, KpFeature> featureGlobal = new ConcurrentHashMap<>();
        featureList.stream().filter(item -> {
            final EmReport.FeatureType featureType = Ut.toEnum(item.getType(), EmReport.FeatureType.class, null);
            if (Objects.isNull(featureType)) {
                return false;
            }
            return EmReport.FeatureType.GLOBAL == featureType;
        }).forEach(feature -> featureGlobal.put(feature.getName(), feature));
        return Ux.future(featureGlobal);
    }

    /**
     * 特征提取
     *
     * @param report 报表定义
     * @return 返回列表
     */
    private Future<List<KpFeature>> featureOfAll(final KpReport report) {
        final JsonObject whereJ = Ux.whereAnd();
        whereJ.put("reportId", report.getKey());
        return DB.on(KpFeatureDao.class).fetchAsync(whereJ);
    }

    private Future<List<KpFeature>> featureOfDim(final KpReport report, final List<KpFeature> featureList) {
        final JsonObject reportConfig = Ut.toJObject(report.getReportConfig());
        final JsonArray featureA = Ut.valueJArray(reportConfig, "feature");
        final ConcurrentMap<String, KpFeature> featureMap = Ut.elementMap(featureList, KpFeature::getName);
        Ut.itJArray(featureA, String.class).forEach(featureName -> {
            if (!featureMap.containsKey(featureName)) {
                final KpFeature feature = featureMap.getOrDefault(featureName, null);
                if (Objects.nonNull(feature)) {
                    featureList.add(feature);
                }
            }
        });
        return Ux.future(featureList);
    }


    /**
     * 维度处理
     *
     * @param report 报表定义
     * @param params 参
     * @return 返回 Map
     */
    private Future<ConcurrentMap<String, RDimension>> reportOfDim(final KpReport report, final JsonObject params) {
        final JsonObject whereJ = Ux.whereAnd();
        whereJ.put("reportId", report.getKey());
        return DB.on(KpDimensionDao.class).<KpDimension>fetchAsync(whereJ).compose(dimensions -> {
            final DimProc processor = DimProc.of();
            return processor.dimAsync(params, dimensions);
        }).compose(dimensions -> {
            final ConcurrentMap<String, RDimension> result = Ut.elementMap(dimensions, RDimension::key);
            return Ux.future(result);
        });
    }
}
