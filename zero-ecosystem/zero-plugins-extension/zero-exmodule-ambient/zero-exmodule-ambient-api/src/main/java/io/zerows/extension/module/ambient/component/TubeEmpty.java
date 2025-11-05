package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.domain.tables.pojos.XActivityRule;
import io.zerows.program.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TubeEmpty implements Tube {
    @Override
    public Future<JsonObject> traceAsync(final JsonObject data, final XActivityRule rule) {
        return Ux.future(data);
    }
}
