package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.common.em.TubeType;
import io.zerows.extension.module.ambient.domain.tables.pojos.XActivityRule;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TubePhase extends TubeBase {
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
