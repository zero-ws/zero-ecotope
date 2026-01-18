package io.zerows.plugins.security.email.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

interface ERR {
    VertxE _80351 = VertxE.of(-80351).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80352 = VertxE.of(-80352).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80353 = VertxE.of(-80353).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
