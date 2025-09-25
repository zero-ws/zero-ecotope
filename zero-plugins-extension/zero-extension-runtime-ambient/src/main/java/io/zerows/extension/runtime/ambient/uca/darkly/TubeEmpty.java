package io.zerows.extension.runtime.ambient.uca.darkly;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivityRule;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TubeEmpty implements Tube {
    @Override
    public Future<JsonObject> traceAsync(final JsonObject data, final XActivityRule rule) {
        return Ux.future(data);
    }
}
