package io.zerows.plugins.security.oauth2.server;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import lombok.extern.slf4j.Slf4j;

@Queue
@Slf4j
public class CallbackActor {

    @Address(Addr.BACK_CLIENT)
    public Future<JsonObject> handleCallback(final JsonObject params) {
        // 这里的逻辑根据之前的 ClientService 实现即可
        return Future.succeededFuture(params);
    }
}