package io.zerows.corpus.plugins.job.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
public interface ERR {
    VertxE _60042 = VertxE.of(-60042).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _60054 = VertxE.of(-60054).state(HttpResponseStatus.CONFLICT);
    VertxE _60041 = VertxE.of(-60041).state(HttpResponseStatus.EXPECTATION_FAILED);
}
