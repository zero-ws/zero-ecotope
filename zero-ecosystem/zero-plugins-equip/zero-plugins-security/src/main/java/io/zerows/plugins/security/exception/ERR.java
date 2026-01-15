package io.zerows.plugins.security.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

interface ERR {
    VertxE _80203 = VertxE.of(-80203).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80204 = VertxE.of(-80204).state(HttpResponseStatus.UNAUTHORIZED);

    VertxE _80212 = VertxE.of(-80212).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80213 = VertxE.of(-80213).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80216 = VertxE.of(-80216).state(HttpResponseStatus.FORBIDDEN);

    VertxE _80240 = VertxE.of(-80240).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80241 = VertxE.of(-80241).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80242 = VertxE.of(-80242).state(HttpResponseStatus.BAD_REQUEST);

    VertxE _80243 = VertxE.of(-80243).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80244 = VertxE.of(-80244).state(HttpResponseStatus.UNAUTHORIZED);
}
