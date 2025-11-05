package io.zerows.extension.module.report.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.RpConstant;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;
import io.zerows.support.Ut;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-27
 */
public interface FeatureO {

    Cc<String, FeatureO> CC_SKELETON = Cc.openThread();

    static FeatureO of(final String valuePath) {
        Objects.requireNonNull(valuePath);
        if (valuePath.startsWith(RpConstant.ValuePath.PREFIX_PARAM)) {
            return CC_SKELETON.pick(FeatureOParam::new, valuePath);
        }
        if (valuePath.startsWith(RpConstant.ValuePath.PREFIX_CLASS)) {
            return CC_SKELETON.pick(FeatureOClass::new, valuePath);
        }
        if (valuePath.startsWith(RpConstant.ValuePath.PREFIX_REFER)) {
            return CC_SKELETON.pick(FeatureORefer::new, valuePath);
        }
        return CC_SKELETON.pick(FeatureOData::new, valuePath);
    }

    /**
     * <pre><code>
     *     params -> data 数据
     *               input 输入参数
     * </code></pre>
     *
     * @return 计算的值
     */
    Future<ConcurrentMap<String, Object>> outAsync(JsonArray dataSource, JsonObject params,
                                                   KpFeature feature);

    interface T {

        static Object formatValue(final Object value, final JsonObject valueConfig) {
            final Object valueResult;
            if (Ut.isNotNil(valueConfig) && valueConfig.containsKey(KName.FORMAT)) {
                final String pattern = Ut.valueString(valueConfig, KName.FORMAT);
                // 时间格式
                if (value instanceof final Instant valueInstant) {
                    final LocalDateTime parsed = Ut.toDateTime(valueInstant);
                    valueResult = Ut.fromDate(parsed, pattern);
                } else {
                    final LocalDateTime parsed = Ut.toDateTime(Ut.parseFull(value.toString()));
                    valueResult = Ut.fromDate(parsed, pattern);
                }
            } else {
                valueResult = value;
            }
            return valueResult;
        }
    }
}
