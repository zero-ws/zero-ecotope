package io.zerows.cosmic.plugins.validation.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
interface ERR {
    // Web 类
    VertxE _60000 = VertxE.of(-60000).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60005 = VertxE.of(-60005).state(HttpResponseStatus.BAD_REQUEST);
}
