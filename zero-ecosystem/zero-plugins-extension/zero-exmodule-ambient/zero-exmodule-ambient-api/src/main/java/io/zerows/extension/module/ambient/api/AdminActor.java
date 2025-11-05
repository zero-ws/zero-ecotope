package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UArray;
import io.zerows.extension.module.ambient.service.AppStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-07-26
 */
@Queue
public class AdminActor {

    @Inject
    private AppStub appStub;

    @Address(Addr.App.ADMIN_USABLE)
    public Future<JsonArray> fetchUsable(final String tenantId) {
        if (Ut.isNil(tenantId)) {
            return Ux.futureA();
        }
        return this.appStub.fetchByTenant(tenantId).compose(appData -> UArray
            .create(appData)
            .remove(
                KName.APP_SECRET,
                KName.APP_KEY
            ).toFuture());
    }
}
