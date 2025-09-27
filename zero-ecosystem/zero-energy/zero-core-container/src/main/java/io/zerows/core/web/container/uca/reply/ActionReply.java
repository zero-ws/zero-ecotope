package io.zerows.core.web.container.uca.reply;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.model.commune.Envelop;

/**
 * @author lang : 2024-06-27
 */
public class ActionReply implements OAmbit {

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        /*
         * DataRegion 启用和禁用处理
         * - 1. Bound Data 执行处理
         * - 2. projection 影响
         * - 3. rows 影响
         */
        final HttpStatusCode code = envelop.status();
        if (HttpStatusCode.OK != code) {
            // 直接返回
            return Future.succeededFuture(envelop);
        }
        // 非直接返回，OK 的场景才生效
        return OAmbit.of(AmbitRegionAfter.class).then(context, envelop);
    }
}
