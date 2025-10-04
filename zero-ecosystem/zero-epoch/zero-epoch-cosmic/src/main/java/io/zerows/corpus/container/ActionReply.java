package io.zerows.corpus.container;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;

/**
 * @author lang : 2024-06-27
 */
public class ActionReply implements Ambit {

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
        // 非直接返回，OK 的场景才生效
        return Ambit.of(AmbitRegionAfter.class).then(context, envelop);
    }
}
