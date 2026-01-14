package io.zerows.plugins.elasticsearch.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _20006 = VertxE.of(-20006).state(HttpResponseStatus.NOT_FOUND);
    VertxE _20007 = VertxE.of(-20007).state(HttpResponseStatus.NOT_FOUND);
    VertxE _20008 = VertxE.of(-20008).state(HttpResponseStatus.NOT_FOUND);
}
