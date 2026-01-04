package io.zerows.plugins.security.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

interface ERR {
    VertxE _80222 = VertxE.of(-80222).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80212 = VertxE.of(-80212).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
