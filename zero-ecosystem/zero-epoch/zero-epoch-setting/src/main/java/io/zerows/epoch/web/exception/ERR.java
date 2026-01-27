package io.zerows.epoch.web.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
public interface ERR {
    VertxE _40032 = VertxE.of(-40032).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60049 = VertxE.of(-60049).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
