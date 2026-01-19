package io.zerows.plugins.security.weco.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-12-09
 */
public interface ERR {
    // 微信
    VertxE _81502 = VertxE.of(-81502).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _81503 = VertxE.of(-81503).state(HttpResponseStatus.UNAUTHORIZED);
    // 企微
    VertxE _81552 = VertxE.of(-81552).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _81553 = VertxE.of(-81553).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _81554 = VertxE.of(-81554).state(HttpResponseStatus.NOT_IMPLEMENTED);
}
