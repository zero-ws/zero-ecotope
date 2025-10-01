package io.zerows.extension.runtime.report.uca.pull;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.program.Kv;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.unity.Ux;

import java.util.Objects;

/**
 * @author lang : 2024-11-04
 */
class DataInputRequest implements DataInput {
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
        if (Objects.nonNull(feature)) {
            this.logger().warn("This component require your input feature = null, but now it''s invalid.");
            return Ux.future();
        }

        final String inputField = Ut.valueString(configureJ, KName.INPUT);
        final Object value = params.getValue(inputField);

        final String outputField = Ut.valueString(configureJ, KName.OUTPUT);
        return Ux.future(Kv.create(outputField, value));
    }
}
