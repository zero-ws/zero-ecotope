package io.zerows.extension.module.workflow.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _80600 = VertxE.of(-80600).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80601 = VertxE.of(-80601).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _80602 = VertxE.of(-80602).state(HttpResponseStatus.CONFLICT);
    VertxE _80603 = VertxE.of(-80603).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80604 = VertxE.of(-80604).state(HttpResponseStatus.CONFLICT);
    VertxE _80605 = VertxE.of(-80605).state(HttpResponseStatus.CONFLICT);
    VertxE _80606 = VertxE.of(-80606).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80607 = VertxE.of(-80607).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80608 = VertxE.of(-80608).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _80609 = VertxE.of(-80609).state(HttpResponseStatus.CONFLICT);
    VertxE _80610 = VertxE.of(-80610).state(HttpResponseStatus.CONFLICT);
    VertxE _80611 = VertxE.of(-80611).state(HttpResponseStatus.GONE);
}
