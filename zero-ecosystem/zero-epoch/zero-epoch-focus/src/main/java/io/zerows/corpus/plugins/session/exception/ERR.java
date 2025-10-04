package io.zerows.corpus.plugins.session.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
interface ERR {
    VertxE _20005 = VertxE.of(-20005).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
