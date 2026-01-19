package io.zerows.plugins.email.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

interface ERR {
    VertxE _80364 = VertxE.of(-80364).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80365 = VertxE.of(-80365).state(HttpResponseStatus.NOT_FOUND);
}
