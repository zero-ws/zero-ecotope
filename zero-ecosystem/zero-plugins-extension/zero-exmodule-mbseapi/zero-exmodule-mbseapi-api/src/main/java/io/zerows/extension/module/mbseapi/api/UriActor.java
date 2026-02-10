package io.zerows.extension.module.mbseapi.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.management.uri.UriAeon;
import io.zerows.epoch.annotations.Queue;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
@Deprecated
public class UriActor {

    public Future<Boolean> createUri(final JsonObject body) {
        UriAeon.mountRoute(body);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
