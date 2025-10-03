package io.zerows.epoch.boot.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
interface ERR {
    VertxE _40001 = VertxE.of(-40001).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40002 = VertxE.of(-40002).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
