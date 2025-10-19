package io.zerows.extension.commerce.rbac.agent.api;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.commerce.rbac.agent.service.accredit.ActionStub;
import io.zerows.extension.commerce.rbac.agent.service.view.PersonalStub;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SViewDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.commerce.rbac.eon.Addr;
import io.zerows.extension.runtime.skeleton.eon.em.OwnerType;
import io.zerows.platform.constant.VName;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Queue
public class ViewMyActor {

    @Inject
    private transient PersonalStub personalStub;
    @Inject
    private transient ActionStub actionStub;

    // ------------------- Personal View
    @Address(Addr.View.VIEW_P_ADD)
    public Future<JsonObject> pViewCreate(final Envelop envelop) {
        /*
         * name, title, projection, criteria
         */
        return this.pAction(envelop).compose(action -> {
            final JsonObject data = Ux.getJson(envelop);
            final String userId = envelop.userId();
            final JsonObject normalized = data.copy();
            normalized.put(KName.USER, userId);
            normalized.mergeIn(envelop.headersX());
            // Bind the resourceId when create new
            normalized.put(KName.RESOURCE_ID, action.getResourceId());
            normalized.put("owner", userId);
            normalized.put("ownerType", OwnerType.USER.name());
            return this.personalStub.create(normalized).compose(Ux::futureJ);
        });
    }

    @Address(Addr.View.VIEW_P_DELETE)
    public Future<Boolean> pViewDelete(final String key) {
        final Set<String> keys = new HashSet<>();
        keys.add(key);
        return this.personalStub.delete(keys);
    }

    @Address(Addr.View.VIEW_P_BY_USER)
    public Future<JsonArray> pViewByUser(final Envelop envelop) {

        return this.pAction(envelop).compose(action -> {
            if (Objects.isNull(action)) {
                return Ux.futureA();
            } else {
                final JsonObject data = Ux.getJson(envelop);
                final String userId = envelop.userId();
                return this.personalStub.byUser(action.getResourceId(), userId,
                        data.getString(KName.POSITION))
                    .compose(Ux::futureA)
                    .compose(Fx.ofJArray(VName.KEY_CRITERIA, VName.KEY_PROJECTION, KName.Rbac.ROWS));
            }
        });
    }

    @Address(Addr.View.VIEW_P_EXISTING)
    public Future<Boolean> pViewExisting(final Envelop envelop) {
        return this.pAction(envelop).compose(action -> {
            if (Objects.isNull(action)) {
                return Future.succeededFuture(Boolean.FALSE);
            } else {
                final JsonObject data = Ux.getJson(envelop);
                final String userId = envelop.userId();
                /*
                 * condition
                 */
                final JsonObject criteria = new JsonObject();
                criteria.mergeIn(data.copy());
                criteria.remove(KName.URI);
                criteria.remove(KName.METHOD);
                criteria.put("owner", userId);
                criteria.put("ownerType", OwnerType.USER.name());
                return DB.on(SViewDao.class).existAsync(criteria);
            }
        });
    }

    private Future<SAction> pAction(final Envelop envelop) {
        final JsonObject header = envelop.headersX();
        final String sigma = header.getString(KName.SIGMA);

        final JsonObject data = Ux.getJson(envelop);
        final String uri = data.getString(KName.URI);
        final HttpMethod method = HttpMethod.valueOf(data.getString(KName.METHOD));

        return this.actionStub.fetchAction(uri, method, sigma);
    }

    @Address(Addr.View.VIEW_P_BY_ID)
    public Future<JsonObject> pViewById(final String key) {
        return this.personalStub.byId(key)
            .compose(Ux::futureJ)
            .compose(Fx.ofJObject(VName.KEY_CRITERIA, VName.KEY_PROJECTION, "rows"));
    }

    @Address(Addr.View.VIEW_P_UPDATE)
    public Future<JsonObject> pViewUpdate(final Envelop envelop) {
        /*
         * name, title, projection, criteria
         */
        final String key = Ux.getString(envelop);
        final JsonObject data = Ux.getJson1(envelop);
        final String userId = envelop.userId();
        data.put(KName.USER, userId);
        return this.personalStub.update(key, data)
            .compose(Ux::futureJ)
            .compose(Fx.ofJObject(VName.KEY_CRITERIA, VName.KEY_PROJECTION, "rows"));
    }


    @Address(Addr.View.VIEW_P_BATCH_DELETE)
    public Future<Boolean> pViewsDelete(final JsonArray keys) {
        return this.personalStub.delete(Ut.toSet(keys));
    }
}
