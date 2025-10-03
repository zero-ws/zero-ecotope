package io.zerows.epoch.corpus.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _40017 = VertxE.of(-40017).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40018 = VertxE.of(-40018).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40047 = VertxE.of(-40047).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40051 = VertxE.of(-40051).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    // Web 类型
    VertxE _60006 = VertxE.of(-60006).state(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE);
    VertxE _60027 = VertxE.of(-60027).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _60048 = VertxE.of(-60048).state(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE);
    VertxE _60051 = VertxE.of(-60051).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80306 = VertxE.of(-80306).state(HttpResponseStatus.FORBIDDEN);
}
