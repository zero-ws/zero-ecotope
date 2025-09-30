package io.zerows.extension.mbse.action.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _80401 = VertxE.of(-80401).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _80402 = VertxE.of(-80402).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _80403 = VertxE.of(-80403).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80404 = VertxE.of(-80404).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80405 = VertxE.of(-80405).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80406 = VertxE.of(-80406).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80407 = VertxE.of(-80407).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _80408 = VertxE.of(-80408).state(HttpResponseStatus.FAILED_DEPENDENCY);
    VertxE _80409 = VertxE.of(-80409).state(HttpResponseStatus.FAILED_DEPENDENCY);
    VertxE _80410 = VertxE.of(-80410).state(HttpResponseStatus.FAILED_DEPENDENCY);
    VertxE _80412 = VertxE.of(-80412).state(HttpResponseStatus.NOT_IMPLEMENTED);
}
