package io.zerows.extension.runtime.ambient.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _80300 = VertxE.of(-80300).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80301 = VertxE.of(-80301).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80302 = VertxE.of(-80302).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80303 = VertxE.of(-80303).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80304 = VertxE.of(-80304).state(HttpResponseStatus.CONFLICT);
    VertxE _80305 = VertxE.of(-80305).state(HttpResponseStatus.NOT_IMPLEMENTED);
}
