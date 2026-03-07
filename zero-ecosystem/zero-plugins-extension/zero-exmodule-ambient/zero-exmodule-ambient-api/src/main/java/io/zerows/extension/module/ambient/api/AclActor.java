package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.ambient.servicespec.MenuStub;
import jakarta.inject.Inject;

@Queue
public class AclActor {

    @Inject
    private transient MenuStub menuStub;

    @Address(Addr.Menu.ACL_FOR_MENU)
    public Future<JsonArray> fetchMenus(final String appId) {
        return this.menuStub.fetchByApp(appId);
    }
}
