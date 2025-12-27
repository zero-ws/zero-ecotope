package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.boot.At;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.domain.tables.daos.XSourceDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.servicespec.AppStub;
import io.zerows.extension.skeleton.spi.ExApp;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

public class AppService implements AppStub {

    @Override
    public Future<JsonArray> fetchByTenant(final String tenantId) {
        return DB.on(XAppDao.class)
            .<XApp>fetchAsync(KName.TENANT_ID, tenantId)
            .compose(Ux::futureA);
    }

    @Override
    public Future<JsonObject> fetchByName(final String name) {
        return DB.on(XAppDao.class)
            /* Fetch By Name */
            .<XApp>fetchOneAsync(KName.NAME, name)
            /* Convert to InJson */
            .compose(Ux::futureJ)
            /* Before App Initialized ( Public Api ) */
            .compose(appData -> {
                appData.remove(KName.APP_KEY);
                appData.remove(KName.APP_SECRET);
                return Ux.future(appData);
            })
            /*
             * Storage of file definition, here are two parts:
             * 1) - Logo            to Object
             * 2) - storePath       extract from `configuration.json`
             * */
            .compose(At::fileMeta)
            /* Modulat Processing */
            .compose(appJ -> HPI.of(ExModulat.class).waitAsync(
                stub -> stub.extension(appJ, true),
                () -> appJ
            ));
    }

    @Override
    public Future<JsonObject> fetchById(final String appId) {
        return DB.on(XAppDao.class)
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
            .compose(appJ -> HPI.of(ExApp.class).waitAsync(
                stub -> stub.fetchOpts(appJ),
                () -> appJ
            ))
            /* Modulat Processing */
            .compose(appJ -> HPI.of(ExModulat.class).waitAsync(
                stub -> stub.extension(appJ, false),
                () -> appJ
            ));
        /* Document Platform Initialized */
        // .compose(appJ -> AtPin.?nitDocument(id).compose(nil -> Ux.future(appJ)));
    }

    @Override
    public Future<JsonObject> fetchSource(final String appId) {
        return DB.on(XSourceDao.class)
            /* Fetch One by id */
            .fetchOneAsync(KName.APP_ID, appId)
            /* Get Result */
            .compose(Ux::futureJ)
            /* JDBC */
            .compose(Fx.ofJObject("jdbcConfig"));
    }

    @Override
    public Future<JsonObject> updateBy(final String appId, final JsonObject data) {
        return this.updateLogo(appId, data)
            .compose(updated -> DB.on(XAppDao.class).updateJAsync(appId, updated)
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
        return HPI.of(ExAttachment.class).waitAsync(
            file -> file.saveAsync(condition, attachment).compose(saved -> {
                data.put(KName.App.LOGO, saved.encode());
                return Ux.future(data);
            }),
            () -> data
        );
    }
}
