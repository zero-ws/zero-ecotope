package io.zerows.plugins.security.email.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

interface ERR {
    VertxE _80361 = VertxE.of(-80361).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80362 = VertxE.of(-80362).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80363 = VertxE.of(-80363).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
