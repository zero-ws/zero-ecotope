package io.zerows.core.exception.web;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-29
 */
interface ERR {
    VertxE _60007 = VertxE.of(-60007).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60023 = VertxE.of(-60023).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60024 = VertxE.of(-60024).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60025 = VertxE.of(-60025).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60026 = VertxE.of(-60026).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60050 = VertxE.of(-60050).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _60059 = VertxE.of(-60059).state(HttpResponseStatus.PRECONDITION_FAILED);
    VertxE _60060 = VertxE.of(-60060).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80413 = VertxE.of(-80413).state(HttpResponseStatus.NOT_IMPLEMENTED);
}
