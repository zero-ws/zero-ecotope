package io.zerows.extension.module.report.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _80700 = VertxE.of(-80700).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _80701 = VertxE.of(-80701).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80702 = VertxE.of(-80702).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80703 = VertxE.of(-80703).state(HttpResponseStatus.BAD_REQUEST);
}
