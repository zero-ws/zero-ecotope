package io.zerows.module.metadata.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _60040 = VertxE.of(-60040).state(HttpResponseStatus.PRECONDITION_FAILED);
}
