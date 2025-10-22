package io.zerows.extension.commerce.rbac.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KView;
import io.zerows.epoch.metadata.security.DataBound;
import io.zerows.extension.commerce.rbac.atom.ScOwner;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.commerce.rbac.uca.acl.rapier.Quinn;
import io.zerows.extension.commerce.rbac.uca.logged.ScUser;
import io.zerows.extension.skeleton.common.enums.OwnerType;
import io.zerows.extension.skeleton.spi.UiAnchoret;
import io.zerows.extension.skeleton.spi.UiApeakMy;
import io.zerows.platform.constant.VName;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

public class ExColumnApeakMy extends UiAnchoret<UiApeakMy> implements UiApeakMy {

    @Override
    public Future<JsonArray> fetchMy(final JsonObject params) {
        final String resourceId = params.getString(UiApeakMy.ARG0);
        if (Ut.isNil(resourceId)) {
            return Ux.futureA();
        }
        final String userId = params.getString(UiApeakMy.ARG1);
        final KView view = KView.smart(params.getValue(UiApeakMy.ARG2));
        // DataBound Building
        final ScOwner owner = new ScOwner(userId, OwnerType.USER);
        owner.bind(view);
        // ScResource building
        return Quinn.vivid().<DataBound>fetchAsync(resourceId, owner).compose(bound -> {
            final JsonArray projection = bound.vProjection();
            /*
             * No view found                        -> []
             * View found and findRunning projection        -> [?,?,...]
             */
            return Ux.future(projection);
        });
    }

    @Override
    public Future<JsonObject> saveMy(final JsonObject params, final JsonObject viewInput) {
        final String resourceId = params.getString(UiApeakMy.ARG0);
        if (Ut.isNil(resourceId)) {
            return Ux.futureJ();
        }
        final String userId = params.getString(UiApeakMy.ARG1);
        /* Normalize data for `language` and `sigma` etc.*/
        final JsonObject viewData = params.copy();

        final ScOwner owner = new ScOwner(userId, OwnerType.USER);
        final KView vis = KView.smart(viewData.getValue(KName.VIEW));
        owner.bind(vis);
        /* Two Params: projection, criteria, rows */
        Ut.valueCopy(viewData, viewInput,
            VName.KEY_PROJECTION,
            VName.KEY_CRITERIA,
            KName.Rbac.ROWS
        );
        viewData.put(KName.UPDATED_BY, userId);     // updatedBy = userId
        /* Save View */
        return Quinn.vivid().<JsonObject>saveAsync(resourceId, owner, viewData)
            /*
             * Flush cache of session join impacted uri
             * This method is for projection refresh here
             * /api/columns/{actor}/my -> save projection join
             * /api/{actor}/search
             * This impact will be in time when this method called.
             * The method is used in this class only and could not be shared.
             */
            .compose(flushed -> this.flushImpact(params, flushed))
            /*
             * Here should flush the key of
             */
            .compose(flushed -> this.flushMy(params, flushed));
    }

    private Future<JsonObject> flushMy(final JsonObject params, final JsonObject updated) {

        return Ux.futureJ(updated);
    }

    private Future<JsonObject> flushImpact(final JsonObject params, final JsonObject updated) {
        /*
         * ScHabitus instance
         */
        final String habitus = params.getString(UiApeakMy.ARG3);
        final ScUser user = ScUser.login(habitus);
        /*
         * Method / Uri
         */
        final String dataKey = params.getString(UiApeakMy.ARG4);
        /*
         * projection / criteria only
         */
        final JsonObject updatedData = new JsonObject();
        updatedData.put(VName.KEY_PROJECTION, updated.getJsonArray(VName.KEY_PROJECTION));
        updatedData.put(VName.KEY_CRITERIA, updated.getJsonObject(VName.KEY_CRITERIA));
        return user.view(dataKey, updatedData).compose(nil -> {
            LOG.Auth.info(this.getLogger(), AuthMsg.REGION_FLUSH, habitus, dataKey,
                nil.getJsonObject(dataKey, new JsonObject()).encodePrettily());
            return Ux.future(updated);
        });
    }
}
