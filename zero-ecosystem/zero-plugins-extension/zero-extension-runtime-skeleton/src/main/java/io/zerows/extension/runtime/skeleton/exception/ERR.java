package io.zerows.extension.runtime.skeleton.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-29
 */
interface ERR {
    VertxE _60045 = VertxE.of(-60045).state(HttpResponseStatus.BAD_REQUEST);
}
