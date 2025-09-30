package io.zerows.epoch.runtime.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _15002 = VertxE.of(-15002).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _10005 = VertxE.of(-10005).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40020 = VertxE.of(-40020).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40028 = VertxE.of(-40028).state(HttpResponseStatus.SERVICE_UNAVAILABLE);
    VertxE _40049 = VertxE.of(-40049).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60040 = VertxE.of(-60040).state(HttpResponseStatus.PRECONDITION_FAILED);
}
