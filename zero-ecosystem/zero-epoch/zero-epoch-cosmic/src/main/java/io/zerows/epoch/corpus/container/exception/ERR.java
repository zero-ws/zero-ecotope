package io.zerows.epoch.corpus.container.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _40008 = VertxE.of(-40008).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40013 = VertxE.of(-40013).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40014 = VertxE.of(-40014).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40016 = VertxE.of(-40016).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40029 = VertxE.of(-40029).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40030 = VertxE.of(-40030).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40037 = VertxE.of(-40037).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40042 = VertxE.of(-40042).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40064 = VertxE.of(-40064).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    // Web 类型
    VertxE _60002 = VertxE.of(-60002).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60003 = VertxE.of(-60003).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60052 = VertxE.of(-60052).state(HttpResponseStatus.LENGTH_REQUIRED);
    VertxE _80510 = VertxE.of(-80510).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80547 = VertxE.of(-80547).state(HttpResponseStatus.CONFLICT);
}
