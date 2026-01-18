package io.zerows.plugins.security.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

interface ERR {
    VertxE _80200 = VertxE.of(-80200).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80201 = VertxE.of(-80201).state(HttpResponseStatus.UNAUTHORIZED);

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

    VertxE _80245 = VertxE.of(-80245).state(HttpResponseStatus.NOT_FOUND);

    // Gateway Handler
    VertxE _80246 = VertxE.of(-80246).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80247 = VertxE.of(-80247).state(HttpResponseStatus.BAD_REQUEST);

    // Gateway Provider
    VertxE _80248 = VertxE.of(-80248).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80249 = VertxE.of(-80249).state(HttpResponseStatus.CONFLICT);

    // BackendProvider
    VertxE _80250 = VertxE.of(-80250).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80251 = VertxE.of(-80251).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80252 = VertxE.of(-80252).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80253 = VertxE.of(-80253).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80254 = VertxE.of(-80254).state(HttpResponseStatus.UNAUTHORIZED);
}
