package io.zerows.extension.module.report.component;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.plugins.AbstractRInComponent;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-11-04
 */
public class RInComponentValue extends AbstractRInComponent {
    @Override
    public Future<Kv<String, Object>> prepare(final JsonObject params, final JsonObject inConfig) {

        final JsonObject config = Ut.valueJObject(inConfig, "params");
        final String format = Ut.valueString(inConfig, KName.FORMAT);
        final JsonObject parameter = this.buildParameter(config, params);

        final String finalValue = Ut.fromExpression(format, parameter);

        // 外层字段
        return this.buildResult(finalValue, inConfig);
    }
}
