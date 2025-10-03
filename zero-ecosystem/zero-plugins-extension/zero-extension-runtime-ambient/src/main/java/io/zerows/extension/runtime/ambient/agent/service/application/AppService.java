package io.zerows.extension.runtime.ambient.agent.service.application;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.metadata.typed.UObject;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XSourceDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.runtime.ambient.util.At;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExApp;
import io.zerows.extension.runtime.skeleton.osgi.spi.feature.Attachment;
import io.zerows.extension.runtime.skeleton.osgi.spi.modeler.Modulat;

public class AppService implements AppStub {

    @Override
    public Future<JsonArray> fetchByTenant(final String tenantId) {
        return Ux.Jooq.on(XAppDao.class)
            .<XApp>fetchAsync(KName.TENANT_ID, tenantId)
            .compose(Ux::futureA);
    }

    @Override
    public Future<JsonObject> fetchByName(final String name) {
        return Ux.Jooq.on(XAppDao.class)
            /* Fetch By Name */
            .<XApp>fetchOneAsync(KName.NAME, name)
            /* Convert to InJson */
            .compose(Ux::futureJ)
            /* Before App Initialized ( Public Api ) */
            .compose(appData -> UObject.create(appData)
                .remove(
                    KName.APP_KEY,                  // appKey
                    KName.APP_SECRET                // appSecret
                )
                .toFuture()
            )
            /*
             * Storage of file definition, here are two parts:
             * 1) - Logo            to Object
             * 2) - storePath       extract from `configuration.json`
             * */
            .compose(At::fileMeta)
            /* Modulat Processing */
            .compose(appJ -> Ux.channel(Modulat.class, () -> appJ, stub -> stub.extension(appJ, true)));
    }

    @Override
    public Future<JsonObject> fetchById(final String appId) {
        return Ux.Jooq.on(XAppDao.class)
            /* Fetch By Id */
            .<XApp>fetchByIdAsync(appId)
            /* Convert to InJson */
            .compose(Ux::futureJ)
            /*
             * Storage of file definition, here are two parts:
             * 1) - Logo            to Object
             * 2) - storePath       extract from `configuration.json`
             * */
            .compose(At::fileMeta)
            /* ExApp Processing, options for application */
            .compose(appJ -> Ux.channel(ExApp.class, () -> appJ, stub -> stub.fetchOpts(appJ)))
            /* Modulat Processing */
            .compose(appJ -> Ux.channel(Modulat.class, () -> appJ, stub -> stub.extension(appJ, false)));
        /* Document Platform Initialized */
        // .compose(appJ -> AtPin.?nitDocument(appId).compose(nil -> Ux.future(appJ)));
    }

    @Override
    public Future<JsonObject> fetchSource(final String appId) {
        return Ux.Jooq.on(XSourceDao.class)
            /* Fetch One by appId */
            .fetchOneAsync(KName.APP_ID, appId)
            /* Get Result */
            .compose(Ux::futureJ)
            /* JDBC */
            .compose(Fx.ofJObject("jdbcConfig"));
    }

    @Override
    public Future<JsonObject> updateBy(final String appId, final JsonObject data) {
        return this.updateLogo(appId, data)
            .compose(updated -> Ux.Jooq.on(XAppDao.class).updateJAsync(appId, updated)
                /* Image field: logo */
                .compose(Fx.ofJObject(KName.App.LOGO)));
    }

    private Future<JsonObject> updateLogo(final String appId, final JsonObject data) {
        final JsonArray attachment = data.getJsonArray(KName.App.LOGO, new JsonArray());
        // Multi EmApp Needed
        Ut.itJArray(attachment).forEach(each -> each.put(KName.MODEL_KEY, appId));
        final JsonObject condition = new JsonObject();
        condition.put(KName.MODEL_ID, "x.application");
        condition.put(KName.MODEL_CATEGORY, KName.App.LOGO);
        condition.put(KName.MODEL_KEY, appId);
        condition.put(VString.EMPTY, Boolean.TRUE);
        return Ux.channel(Attachment.class, () -> data,
            // Sync Attachment with channel
            file -> file.saveAsync(condition, attachment).compose(saved -> {
                data.put(KName.App.LOGO, saved.encode());
                return Ux.future(data);
            }));
    }
}
