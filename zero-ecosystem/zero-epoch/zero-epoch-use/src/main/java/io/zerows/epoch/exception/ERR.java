package io.zerows.epoch.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _10005 = VertxE.of(-10005).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _15000 = VertxE.of(-15000).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _15002 = VertxE.of(-15002).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40020 = VertxE.of(-40020).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40049 = VertxE.of(-40049).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60004 = VertxE.of(-60004).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60021 = VertxE.of(-60021).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60040 = VertxE.of(-60040).state(HttpResponseStatus.PRECONDITION_FAILED);
    VertxE _60058 = VertxE.of(-60058).state(HttpResponseStatus.CONFLICT);
    VertxE _80223 = VertxE.of(-80223).state(HttpResponseStatus.CONFLICT);
    VertxE _80224 = VertxE.of(-80224).state(HttpResponseStatus.CONFLICT);
    VertxE _80542 = VertxE.of(-80542).state(HttpResponseStatus.CONFLICT);
    VertxE _80543 = VertxE.of(-80543).state(HttpResponseStatus.PRECONDITION_FAILED);
    VertxE _80548 = VertxE.of(-80548).state(HttpResponseStatus.PRECONDITION_FAILED);
}
