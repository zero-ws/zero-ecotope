package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.module.rbac.domain.tables.pojos.SView;
import io.zerows.program.Ux;

@Queue
public class ViewRoleActor {

    @Address(Addr.View.VIEW_R_GET)
    public Future<JsonObject> fetchView(final String owner, final String resourceId,
                                        final KView view) {
        return Ux.futureJ();
    }


    @Address(Addr.View.VIEW_R_SAVE)
    public Future<JsonObject> saveView(final String owner, final String resourceId,
                                       final SView myView, final KView view) {
        return Ux.futureJ();
    }
}
