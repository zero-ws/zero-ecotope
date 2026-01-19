package io.zerows.plugins.sms.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-12-06
 */
interface ERR {
    VertxE _80351 = VertxE.of(-80351).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80352 = VertxE.of(-80352).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80353 = VertxE.of(-80353).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80354 = VertxE.of(-80354).state(HttpResponseStatus.NOT_FOUND);
}
