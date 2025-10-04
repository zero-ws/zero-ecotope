package io.zerows.corpus.plugins.security.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-10-04
 */
interface ERR {
    VertxE _40038 = VertxE.of(-40038).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40040 = VertxE.of(-40040).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40075 = VertxE.of(-40075).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40076 = VertxE.of(-40076).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40077 = VertxE.of(-40077).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40041 = VertxE.of(-40041).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);

}
