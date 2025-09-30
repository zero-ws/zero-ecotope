package io.zerows.epoch.web.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _20005 = VertxE.of(-20005).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40038 = VertxE.of(-40038).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40040 = VertxE.of(-40040).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40041 = VertxE.of(-40041).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40075 = VertxE.of(-40075).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40076 = VertxE.of(-40076).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _40077 = VertxE.of(-40077).state(HttpResponseStatus.BAD_REQUEST);
    // Web 类
    VertxE _60000 = VertxE.of(-60000).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60005 = VertxE.of(-60005).state(HttpResponseStatus.BAD_REQUEST);
    VertxE _60034 = VertxE.of(-60034).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60035 = VertxE.of(-60035).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60041 = VertxE.of(-60041).state(HttpResponseStatus.EXPECTATION_FAILED);
    VertxE _60042 = VertxE.of(-60042).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _60046 = VertxE.of(-60046).state(HttpResponseStatus.NOT_IMPLEMENTED);
    VertxE _60047 = VertxE.of(-60047).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _60054 = VertxE.of(-60054).state(HttpResponseStatus.CONFLICT);
}
