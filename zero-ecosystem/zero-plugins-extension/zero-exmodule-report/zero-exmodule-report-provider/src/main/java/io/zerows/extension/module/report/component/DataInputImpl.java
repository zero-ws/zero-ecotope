package io.zerows.extension.module.report.component;

import io.r2mo.SourceReflect;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.em.EmReport;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-11-04
 */
class DataInputImpl implements DataInput {

    static DataInput of(final HBundle owner, final Class<?> implCls) {
        final String keyCache = HBundle.id(owner, implCls);
        return CC_SKELETON.pick(() -> (DataInput) SourceReflect.instance(implCls), keyCache);
    }

    /**
     * 参数要求
     * <pre><code>
     *     {
     *         "reportAt"
     *     }
     * </code></pre>
     * 上述参数必须存在，如此才可以执行 region 操作
     *
     * @param params     参数
     * @param configureJ 配置
     * @param feature    特征
     *
     * @return 返回值
     */
    @Override
    public Future<Kv<String, Object>> prepare(final JsonObject params, final JsonObject configureJ, final KpFeature feature) {
        // 执行器类型分析
        final String typeStr = Ut.valueString(configureJ, KName.TYPE);
        final EmReport.InputSource type = Ut.toEnum(typeStr, EmReport.InputSource.class, EmReport.InputSource.INPUT);
        if (EmReport.InputSource.FEATURE == type) {
            final DataInput input = of(null, DataInputFeature.class);
            return input.prepare(params, configureJ, feature);
        }
        // Default
        final DataInput input = of(null, DataInputRequest.class);
        return input.prepare(params, configureJ, feature);
    }
}
