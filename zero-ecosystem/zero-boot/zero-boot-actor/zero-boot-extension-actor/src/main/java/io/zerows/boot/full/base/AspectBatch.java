package io.zerows.boot.full.base;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.program.Ux;
import io.zerows.support.Fx;

/**
 *
 */
public class AspectBatch extends AbstractAspect {

    @Override
    public Future<JsonArray> beforeAsync(final JsonArray input,
                                         final JsonObject options) {
        return Ux.future(input, this.queue.beforePlugins(options))
            .otherwise(Fx.otherwiseFn(() -> input));
    }

    @Override
    public Future<JsonArray> afterAsync(final JsonArray input,
                                        final JsonObject options) {
        return Ux.future(input)
            .compose(processed -> Ux.future(processed, this.queue.afterPlugins(options)))   // Sync
            .compose(processed -> Ux.future(processed, this.queue.jobPlugins(options)))     // Async
            .otherwise(Fx.otherwiseFn(() -> input));
    }
}
