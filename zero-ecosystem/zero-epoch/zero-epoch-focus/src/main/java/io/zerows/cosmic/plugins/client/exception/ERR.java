package io.zerows.cosmic.plugins.client.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
interface ERR {
    VertxE _60046 = VertxE.of(-60046).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _60047 = VertxE.of(-60047).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
