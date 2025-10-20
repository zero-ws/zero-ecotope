package io.zerows.extension.runtime.skeleton.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-29
 */
interface ERR {
    VertxE _60045 = VertxE.of(-60045).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80214 = VertxE.of(-80214).state(HttpResponseStatus.EXPECTATION_FAILED);
    VertxE _80219 = VertxE.of(-80219).state(HttpResponseStatus.FORBIDDEN);
    VertxE _81002 = VertxE.of(-81002).state(HttpResponseStatus.BAD_REQUEST);
}
