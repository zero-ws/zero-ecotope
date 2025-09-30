package io.zerows.extension.commerce.rbac.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-30
 */
interface ERR {
    VertxE _80200 = VertxE.of(-80200).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80201 = VertxE.of(-80201).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80202 = VertxE.of(-80202).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80203 = VertxE.of(-80203).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80204 = VertxE.of(-80204).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80206 = VertxE.of(-80206).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80207 = VertxE.of(-80207).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80208 = VertxE.of(-80208).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80209 = VertxE.of(-80209).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80210 = VertxE.of(-80210).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80211 = VertxE.of(-80211).state(HttpResponseStatus.FORBIDDEN);
    VertxE _80215 = VertxE.of(-80215).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _80220 = VertxE.of(-80220).state(HttpResponseStatus.LOCKED);
    VertxE _80221 = VertxE.of(-80221).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80222 = VertxE.of(-80222).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80225 = VertxE.of(-80225).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80226 = VertxE.of(-80226).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80227 = VertxE.of(-80227).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80228 = VertxE.of(-80228).state(HttpResponseStatus.NOT_FOUND);
    VertxE _80229 = VertxE.of(-80229).state(HttpResponseStatus.UNAUTHORIZED);
    VertxE _80230 = VertxE.of(-80230).state(HttpResponseStatus.UNAUTHORIZED);
}
