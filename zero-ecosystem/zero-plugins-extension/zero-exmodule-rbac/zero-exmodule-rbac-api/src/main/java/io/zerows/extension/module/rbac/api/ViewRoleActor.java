package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KView;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.SViewDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SView;
import io.zerows.extension.skeleton.common.enums.OwnerType;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

@Queue
public class ViewRoleActor {

    @Address(Addr.View.VIEW_R_GET)
    public Future<JsonObject> fetchView(final String owner, final String resourceId,
                                        final KView view) {
        return this.fetchViewInternal(owner, resourceId, view)
            .compose(Ux::futureJ);
    }


    @Address(Addr.View.VIEW_R_SAVE)
    public Future<JsonObject> saveView(final String owner, final String resourceId,
                                       final SView myView, final KView view) {
        return this.fetchViewInternal(owner, resourceId, view)
            .compose(stored -> {
                if (Objects.isNull(stored)) {
                    return Ux.futureJ();
                }
                Ut.updateT(stored, Ut.serializeJson(myView));
                return DB.on(SViewDao.class).updateAsync(stored).compose(Ux::futureJ);
            });
    }

    private Future<SView> fetchViewInternal(final String owner, final String resourceId, final KView view) {
        final JsonObject whereJ = this.whereJ(owner, resourceId, view);
        return DB.on(SViewDao.class).fetchOneAsync(whereJ);
    }

    private JsonObject whereJ(final String owner, final String resourceId, final KView view) {
        final JsonObject whereJ = KView.whereJ(view);
        whereJ.put(KName.OWNER_TYPE, OwnerType.ROLE.name());
        whereJ.put(KName.OWNER, owner);
        whereJ.put(KName.RESOURCE_ID, resourceId);
        return whereJ;
    }
}
