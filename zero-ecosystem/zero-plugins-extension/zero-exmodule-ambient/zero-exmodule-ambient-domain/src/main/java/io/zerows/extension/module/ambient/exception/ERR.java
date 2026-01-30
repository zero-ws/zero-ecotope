package io.zerows.extension.module.ambient.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _80300 = VertxE.of(-80300).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80301 = VertxE.of(-80301).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80302 = VertxE.of(-80302).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80303 = VertxE.of(-80303).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80304 = VertxE.of(-80304).state(HttpResponseStatus.CONFLICT);
    VertxE _80305 = VertxE.of(-80305).state(HttpResponseStatus.NOT_IMPLEMENTED);
    // 初始化过程失败
    VertxE _80306 = VertxE.of(-80306).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80307 = VertxE.of(-80307).state(HttpResponseStatus.NOT_IMPLEMENTED);
}
