package io.zerows.plugins.security.sms.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-12-06
 */
interface ERR {

    VertxE _80381 = VertxE.of(-80381).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80382 = VertxE.of(-80382).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80383 = VertxE.of(-80383).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
