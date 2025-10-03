package io.zerows.extension.runtime.report.api.service;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.metadata.Kv;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.program.fn.Fx;
import io.zerows.support.FnBase;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.daos.KpReportInstanceDao;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReport;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;
import io.zerows.extension.runtime.report.uca.combiner.StepGenerator;
import io.zerows.extension.runtime.report.uca.pull.DataInput;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-10-11
 */
public class ReportInstanceService implements ReportInstanceStub {
    @Override
    public Future<JsonObject> searchPaged(final JsonObject query) {
        return Ux.Jooq.on(KpReportInstanceDao.class)
            .searchAsync(query);
    }

    @Override
    public Future<Buffer> exportAsync(final String key) {
        return null;
    }

    @Override
    public Future<JsonObject> saveInstance(final String key, final JsonObject data) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.NAME, Ut.valueString(data, KName.NAME));
        condition.put(KName.SIGMA, Ut.valueString(data, KName.SIGMA));
        return Ux.Jooq.on(KpReportInstanceDao.class)
            .upsertJAsync(condition, data);
    }

    @Override
    public Future<Boolean> deleteInstance(final String key) {
        return Ux.Jooq.on(KpReportInstanceDao.class)
            .deleteByIdAsync(key);
    }

    @Override
    public Future<JsonObject> fetchInstance(final String key) {
        return Ux.Jooq.on(KpReportInstanceDao.class)
            .fetchByIdAsync(key)
            .compose(Ux::futureJ).compose(Fx.ofJObject(
                "reportContent",
                "reportData"
            ));
    }

    /**
     * 生成报表基础格式，基本内容甄别
     * <pre><code>
     *     每一项数据的来源分析
     *     key:    主键，每次生成时没有主键，生成之后保存完成后会有主键
     *     name:   NAME + TIME
     *     status: ACTIVE
     *     type:   CODE
     *
     *     下边属性是为了呈现
     *     title:           根据表达式计算
     *     subtitle:        region 部分 -> 左上角
     *     extra:           extra 不分 -> 右上角
     *     description:     （保留）
     *
     *     modeExpr:        （保留）
     *
     *     reportId:        关联报表 ID
     *     reportContent:   报表生成内容 2维矩阵（唯一需要计算的部分）
     *     reportData:      报表原始数据
     *
     *     // 其他信息在保存时使用
     *     reportBy:
     *     reportAt:
     * </code></pre>
     *
     * @param data       数据部分
     * @param params     参数部分
     * @param generation 生成配置
     *
     * @return 生成好的报表实例
     */
    @Override
    public Future<KpReportInstance> buildAsync(final JsonArray data, final JsonObject params,
                                               final RGeneration generation) {
        // 参数准备
        return this.parameterPrepare(params, generation).compose(paramMap -> {
            // 构造 KpReportInstance
            final JsonObject paramsMiddle = params.copy();
            {
                // 很重要的参数提取
                paramMap.forEach(paramsMiddle::put);
                final String reportStartTime = Ut.valueString(params, "reportStartTime");
                final String reportEndTime = Ut.valueString(params, "reportEndTime");
                if (reportStartTime == null && reportEndTime == null) {
                    final String timeStr = Ut.valueString(params, "reportAt");
                    final String time = Ut.fromDate(Ut.parseFull(timeStr), "yyyy-MM-dd");
                    paramsMiddle.put("time", time);
                } else {
                    final String time = Ut.fromDate(Ut.parseFull(reportStartTime), "yyyy-MM-dd");
                    final String time2 = Ut.fromDate(Ut.parseFull(reportEndTime), "yyyy-MM-dd");
                    paramsMiddle.put("time", time);
                    paramsMiddle.put("time2", time2);
                }


            }


            final StepGenerator generator = StepGenerator.of(generation);
            final KpReportInstance instance = new KpReportInstance();
            // 报表原始数据设置（很重要）
            /*
             * - key
             */
            instance.setKey(UUID.randomUUID().toString());

            return generator.build(instance, paramsMiddle, data);
        });
    }

    /**
     * 参数提取不依赖数据源，配置格式如
     * <pre><code>
     *     {
     *         "pShift": {
     *             "type": "FEATURE",
     *             "name": "pShift",
     *             "output": "pShift"
     *         },
     *         "pRegion": {
     *             "type": "FEATURE",
     *             "name": "pRegion",
     *             "output": "pRegion"
     *         },
     *         "pAt": {
     *             "output": "pAt",
     *             "input": "reportAt"
     *         }
     *     }
     * </code></pre>
     *
     * @param params     参数
     * @param generation 生成配置
     *
     * @return 所有参数信息做输入处理
     */
    private Future<ConcurrentMap<String, Object>> parameterPrepare(final JsonObject params,
                                                                   final RGeneration generation) {
        final KpReport report = generation.reportMeta();
        JsonObject parameterJ = Ut.toJObject(report.getReportParam());
        parameterJ = this.parameterDefault(parameterJ);

        final List<Future<Kv<String, Object>>> futures = new ArrayList<>();
        Ut.<JsonObject>itJObject(parameterJ).forEach(entry -> {
            final DataInput input = DataInput.of();
            final JsonObject configureJ = entry.getValue();
            // 特征提取，会访问已经配置好的特征信息
            final String featureName = Ut.valueString(configureJ, KName.NAME);
            final KpFeature feature = generation.featureGlobal(featureName);
            futures.add(input.prepare(params, configureJ, feature));
        });
        return FnBase.combineT(futures).compose(processed -> {
            final ConcurrentMap<String, Object> paramMap = new ConcurrentHashMap<>();
            processed.forEach(kv -> {
                if (paramMap.containsKey(kv.key())) {
                    Ut.Log.service(this.getClass()).info("Key: {0} has been duplicated in parameter configuration.", kv.key());
                }
                paramMap.put(kv.key(), kv.value());
            });
            return Ux.future(paramMap);
        });
    }

    private JsonObject parameterDefault(final JsonObject parameterJ) {
        final JsonObject normalized = new JsonObject();
        Ut.<JsonObject>itJObject(parameterJ).forEach(entry -> {
            /*
             * {
             *     "type": "FEATURE",
             *     "name": "???",
             *     "output": "???"
             * }
             */
            final JsonObject configureJ = entry.getValue().copy();
            final String parameterName = entry.getKey();

            if (!configureJ.containsKey(KName.NAME)) {
                configureJ.put(KName.NAME, parameterName);
            }

            if (!configureJ.containsKey(KName.OUTPUT)) {
                configureJ.put(KName.OUTPUT, parameterName);
            }
            normalized.put(entry.getKey(), configureJ);
        });
        return normalized;
    }
}
