package io.zerows.epoch.container.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _60002 = VertxE.of(-60002).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60003 = VertxE.of(-60003).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60052 = VertxE.of(-60052).state(HttpResponseStatus.LENGTH_REQUIRED);
}
