package io.zerows.extension.commerce.rbac.agent.service.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SResourceDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SResource;

import java.util.Optional;
import java.util.UUID;

public class ResourceService implements ResourceStub {

    @Override
    public Future<JsonObject> fetchResource(final String resourceId) {
        return DB.on(SResourceDao.class)
            .fetchByIdAsync(resourceId)
            .compose(Ux::futureJ)
            .compose(resource -> DB.on(SActionDao.class)
                .fetchOneAsync(KName.RESOURCE_ID, resourceId)
                .compose(Ux::futureJ)
                .compose(action -> Ux.future(resource.put("action", action))));
    }

    @Override
    public Future<JsonObject> createResource(final JsonObject params) {
        final SResource sResource = Ux.fromJson(params, SResource.class);

        return DB.on(SResourceDao.class)
            .insertAsync(sResource)
            .compose(Ux::futureJ)
            .compose(resource -> {
                // handle action node if present
                if (params.containsKey("action") && Ut.isNotNil(params.getJsonObject("action"))) {
                    final SAction sAction = Ux.fromJson(params.getJsonObject("action"), SAction.class);
                    // verify important fields
                    sAction.setKey(Optional.ofNullable(sAction.getKey()).orElse(UUID.randomUUID().toString()))
                        .setActive(Optional.ofNullable(sAction.getActive()).orElse(Boolean.TRUE))
                        .setResourceId(Optional.ofNullable(sAction.getResourceId()).orElse(resource.getString(KName.KEY)))
                        .setLevel(Optional.ofNullable(sAction.getLevel()).orElse(resource.getInteger("level")))
                        .setSigma(Optional.ofNullable(sAction.getSigma()).orElse(resource.getString(KName.SIGMA)))
                        .setLanguage(Optional.ofNullable(sAction.getLanguage()).orElse(resource.getString(KName.LANGUAGE)));
                    return DB.on(SActionDao.class)
                        .insertAsync(sAction)
                        .compose(Ux::futureJ)
                        .compose(action -> Ux.future(resource.put("action", action)));
                } else {
                    return Ux.future(resource);
                }
            });
    }

    @Override
    public Future<JsonObject> updateResource(final String resourceId, final JsonObject params) {
        final SResource sResource = Ux.fromJson(params, SResource.class);

        return DB.on(SResourceDao.class)
            .upsertAsync(resourceId, sResource)
            .compose(Ux::futureJ)
            .compose(resource -> {
                // handle action node if present
                if (params.containsKey("action") && Ut.isNotNil(params.getJsonObject("action"))) {
                    final SAction sAction = Ux.fromJson(params.getJsonObject("action"), SAction.class);
                    return DB.on(SActionDao.class)
                        .upsertAsync(new JsonObject().put(KName.RESOURCE_ID, resourceId), sAction)
                        .compose(Ux::futureJ)
                        .compose(action -> Ux.future(resource.put("action", action)));
                } else {
                    return Ux.future(resource);
                }
            });
    }

    @Override
    public Future<Boolean> deleteResource(final String resourceId) {
        return DB.on(SActionDao.class)
            .deleteByAsync(new JsonObject().put(KName.RESOURCE_ID, resourceId))
            .compose(result -> DB.on(SResourceDao.class)
                .deleteByIdAsync(resourceId));
    }
}
