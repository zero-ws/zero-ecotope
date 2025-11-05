package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.extension.module.ambient.servicespec.AppStub;
import io.zerows.extension.module.ambient.servicespec.MenuStub;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-07-26
 */
@Queue
public class EntryActor {

    @Inject
    private transient AppStub appStub;

    @Inject
    private transient MenuStub menuStub;

    @Address(Addr.App.BY_NAME)
    public Future<JsonObject> byName(final String name) {
        return this.appStub.fetchByName(name);
    }

    @Address(Addr.App.BY_ID)
    public Future<JsonObject> byId(final String appId, final XHeader header) {
        return this.appStub.fetchById(appId);
    }

    @Address(Addr.Menu.BY_APP_ID)
    public Future<JsonArray> fetchMenus(final String appId) {
        return this.menuStub.fetchByApp(appId);
    }

}
