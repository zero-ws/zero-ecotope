package io.zerows.corpus.plugins.cache.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
interface ERR {
    VertxE _60034 = VertxE.of(-60034).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60035 = VertxE.of(-60035).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
