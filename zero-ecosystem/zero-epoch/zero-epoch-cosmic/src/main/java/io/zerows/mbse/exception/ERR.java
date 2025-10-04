package io.zerows.mbse.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
public interface ERR {
    VertxE _80510 = VertxE.of(-80510).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80547 = VertxE.of(-80547).state(HttpResponseStatus.CONFLICT);
}
