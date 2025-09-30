package io.zerows.plugins.integration.sms.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _20003 = VertxE.of(-20003).state(HttpResponseStatus.FAILED_DEPENDENCY);
    VertxE _20004 = VertxE.of(-20004).state(HttpResponseStatus.FAILED_DEPENDENCY);
}
