package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.skeleton.spi.ExArborBase;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ExArborWhole extends ExArborBase {
    @Override
    public Future<JsonArray> generate(final JsonObject category, final JsonObject configuration) {
        return this.combineArbor(category, null, configuration);
    }
}
