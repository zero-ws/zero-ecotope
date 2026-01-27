package io.zerows.epoch.spec.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
interface ERR {
    // Boot  相关异常
    VertxE _40001 = VertxE.of(-40001).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40002 = VertxE.of(-40002).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    // Cloud 配置相关异常
    VertxE _41001 = VertxE.of(-41001).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _41002 = VertxE.of(-41002).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _41003 = VertxE.of(-41003).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
