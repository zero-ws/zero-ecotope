package io.zerows.extension.runtime.ambient.uca.darkly;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivityRule;
import io.zerows.extension.runtime.ambient.eon.em.TubeType;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TubePhase extends AbstractTube {
    @Override
    public Future<JsonObject> traceAsync(final JsonObject data, final XActivityRule rule) {
        return this.diffAsync(data, rule, KName.PHASE, () -> {
            /*
             * java.lang.StackOverflowError fix
             * Change TubeType.PHASE -> TubeType.EXPRESSION
             */
            final Tube tube = Tube.instance(TubeType.EXPRESSION);
            return tube.traceAsync(data, rule);
        });
    }
}
