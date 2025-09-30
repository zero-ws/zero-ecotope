package io.zerows.plugins.common.shell.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _40070 = VertxE.of(-40070).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40071 = VertxE.of(-40071).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40072 = VertxE.of(-40072).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40073 = VertxE.of(-40073).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40074 = VertxE.of(-40074).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
}
