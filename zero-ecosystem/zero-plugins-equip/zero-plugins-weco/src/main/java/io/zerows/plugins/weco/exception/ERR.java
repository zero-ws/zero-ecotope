package io.zerows.plugins.weco.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-12-06
 */
interface ERR {
    VertxE _81501 = VertxE.of(-81501).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _81551 = VertxE.of(-81551).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
