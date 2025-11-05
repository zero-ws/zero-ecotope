package io.zerows.extension.crud.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _80100 = VertxE.of(-80100).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80102 = VertxE.of(-80102).state(HttpResponseStatus.CONFLICT);
    VertxE _80103 = VertxE.of(-80103).state(HttpResponseStatus.CONFLICT);
}
