package io.zerows.cosmic.webflow;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.jigsaw.ZeroPlugins;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Fx;

import java.util.List;
import java.util.function.Function;

/**
 * @author lang : 2024-06-27
 */
public class AmbitReply implements Ambit {

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        /*
         * DataRegion 启用和禁用处理
         * - 1. Bound Data 执行处理
         * - 2. projection 影响
         * - 3. rows 影响
         */
        final WebState code = envelop.status();
        final HttpResponseStatus status = code.value();
        if (HttpResponseStatus.OK.code() != status.code()) {
            // 直接返回
            return Future.succeededFuture(envelop);
        }
        /*
         *  - After
         */
        return this.afterApply(context, envelop);
    }

    private Future<Envelop> afterApply(final RoutingContext context, final Envelop envelop) {
        final Vertx vertx = context.vertx();

        final List<UnderApply> underApply = ZeroPlugins.of(vertx).createPlugin(UnderApply.class);
        final List<Function<Envelop, Future<Envelop>>> underApplyFn = underApply.stream()
            .map(item -> (Function<Envelop, Future<Envelop>>) (envelopInput -> item.after(context, envelopInput)))
            .toList();
        return Fx.passion(envelop, underApplyFn);
    }
}
