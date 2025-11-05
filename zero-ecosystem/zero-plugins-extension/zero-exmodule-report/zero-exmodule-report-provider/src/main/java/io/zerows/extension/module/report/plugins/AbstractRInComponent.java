package io.zerows.extension.module.report.plugins;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.Rp;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-11-14
 */
public abstract class AbstractRInComponent implements RInComponent {

    protected Future<Kv<String, Object>> buildResult(final Object resultValue,
                                                     final JsonObject inConfig) {
        final JsonObject configureJ = Ut.valueJObject(inConfig, KName.__.INHERIT);
        final String outputField = Ut.valueString(configureJ, KName.OUTPUT);
        return Ux.future(Kv.create(outputField, resultValue));
    }

    protected JsonObject buildParameter(final JsonObject config, final JsonObject params) {
        final JsonObject parsed = new JsonObject();
        Ut.itJObject(config, String.class).forEach(entry -> {
            final String name = entry.getKey();
            final String source = entry.getValue();
            final Object value = Rp.valueFormat(source, params);
            parsed.put(name, value);
        });
        return parsed;
    }
}
