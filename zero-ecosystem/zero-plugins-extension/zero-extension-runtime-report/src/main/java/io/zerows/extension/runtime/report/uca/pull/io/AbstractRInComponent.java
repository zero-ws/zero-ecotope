package io.zerows.extension.runtime.report.uca.pull.io;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.component.shared.program.Kv;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.refine.Rp;
import io.zerows.extension.runtime.report.uca.feature.RInComponent;

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
