package io.zerows.extension.mbse.modulat.uca.notify;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Subscribe;
import io.zerows.epoch.annotations.Address;
import io.zerows.extension.mbse.modulat.eon.Addr;

/**
 * @author lang : 2024-04-09
 */
public class NotifyParameters {

    @Subscribe("block-configure")
    @Address(Addr.Notify.BLOCK_CONFIGURE_UP)
    public Future<JsonObject> sendBack(final JsonObject parameters) {
        return Future.succeededFuture(parameters);
    }
}
