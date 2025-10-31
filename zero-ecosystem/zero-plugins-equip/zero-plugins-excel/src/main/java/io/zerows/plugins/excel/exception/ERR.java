package io.zerows.plugins.excel.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _60037 = VertxE.of(-60037).state(HttpResponseStatus.NOT_FOUND);
    VertxE _60038 = VertxE.of(-60038).state(HttpResponseStatus.NOT_FOUND);
    VertxE _60039 = VertxE.of(-60039).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
