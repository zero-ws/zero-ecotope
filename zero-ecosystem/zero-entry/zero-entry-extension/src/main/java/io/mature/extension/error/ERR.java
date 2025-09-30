package io.mature.extension.error;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _81000 = VertxE.of(-81000).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _81001 = VertxE.of(-81001).state(HttpResponseStatus.NOT_IMPLEMENTED);
}
