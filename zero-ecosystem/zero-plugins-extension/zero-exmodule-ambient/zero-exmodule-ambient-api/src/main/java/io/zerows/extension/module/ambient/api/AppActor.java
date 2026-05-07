package io.zerows.extension.module.ambient.api;

import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XNoticeDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XNotice;
import io.zerows.extension.module.ambient.servicespec.AppStub;
import io.zerows.extension.module.ambient.servicespec.InitStub;
import io.zerows.platform.apps.KDS;
import io.zerows.program.Ux;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.function.Function;

@Queue
public class AppActor {

    @Inject
    private transient AppStub appStub;

    @Inject
    private transient InitStub stub;

    @Address(Addr.Init.INIT)
    public Future<JsonObject> initApp(final String appId, final JsonObject data) {
        return this.stub.initCreation(appId, data);
    }

    @Address(Addr.Init.PREPARE)
    public Future<JsonObject> prepare(final String appName) {
        return this.stub.prerequisite(appName);
    }

    @Address(Addr.App.UP_BY_ID)
    public Future<JsonObject> updateBy(final String appId, final JsonObject data) {
        return this.appStub.updateBy(appId, data);
    }


    /*
     * Fetch all datasource
     * {
     *      "database": "",
     *      "history": "",
     *      "workflow": "",
     *      "argument": ""
     * }
     */
    @Address(Addr.Init.SOURCE)
    public Future<JsonObject> database(final String appId) {
        return this.appStub.fetchSource(appId)
            .compose(atom -> Ux.future(toDatabaseResponse(KDS.of(), atom)));
    }

    static JsonObject toDatabaseResponse(final KDS kds, final JsonObject atom) {
        final JsonObject response = new JsonObject();
        response.put("database", sanitizeDbs(kds.findRunning(DBMany.DEFAULT_DBS)));
        response.put("history", sanitizeDbs(kds.findRunning(KDS.DEFAULT_DBS_HISTORY)));
        response.put("workflow", sanitizeDbs(kds.findRunning(KDS.DEFAULT_DBS_WORKFLOW)));
        response.put("argument", sanitizeJson(atom));
        return response;
    }

    static JsonObject sanitizeDbs(final DBS dbs) {
        if (null == dbs) {
            return null;
        }
        final Database database = dbs.getDatabase();
        if (null == database) {
            return null;
        }
        final JsonObject json = new JsonObject();
        if (null != database.getType()) {
            json.put("category", database.getType().name());
        }
        json.put("hostname", database.getHostname());
        json.put("port", database.getPort());
        json.put("instance", database.getInstance());
        json.put("url", database.getUrl());
        json.put("driver-class-name", database.getDriverClassName());
        json.put("driverClassName", database.getDriverClassName());
        if (null != database.getOptions()) {
            json.put("options", new JsonObject(database.getOptions().encode()));
        }
        return sanitizeJson(json);
    }

    static JsonObject sanitizeJson(final JsonObject json) {
        if (null == json) {
            return null;
        }
        final JsonObject copied = json.copy();
        copied.remove(KName.PASSWORD);
        copied.remove(KName.USERNAME);
        return copied;
    }

    /*
     * New interface for expired notices updating
     */
    @Address(Addr.Init.NOTICE)
    public Future<JsonArray> notice(final String appId, final JsonObject criteria) {
        // ExpiredAt Updating first
        final JsonObject expiredQr = Ux.whereAnd();
        expiredQr.put("expiredAt,<", Instant.now());
        expiredQr.put(KName.APP_ID, appId);
        final ADB jq = DB.on(XNoticeDao.class);
        return jq.<XNotice>fetchAsync(expiredQr).compose(notices -> {
            // Turn Off the expired notices
            notices.forEach(notice -> notice.setStatus("FINISHED"));
            return jq.updateAsync(notices);
        }).compose(nil -> {
            // Query again
            final JsonObject dashboardQr = Ux.whereAnd();
            dashboardQr.put(KName.APP_ID, appId);
            dashboardQr.put("$IN$", criteria);
            return jq.fetchJAsync(dashboardQr);
        });
    }

    @Address(Addr.Init.DEPLOY)
    public Future<JsonObject> deploy(final JsonObject request) {
        return this.stub.deploy(request);
    }

    @Address(Addr.Init.START)
    public Future<JsonObject> start(final JsonObject request) {
        return this.stub.start(request);
    }

    @Address(Addr.Init.START)
    public Future<JsonObject> startByKey(final String instanceKey, final JsonObject request) {
        final JsonObject normalized = request == null ? new JsonObject() : request.copy();
        normalized.put("instanceKey", instanceKey);
        return this.stub.start(normalized);
    }

    @Address(Addr.Init.HEALTH_CHECK)
    public Future<JsonObject> healthCheck(final String instanceKey) {
        return this.stub.healthCheck(instanceKey);
    }

    @Address(Addr.Init.UNDEPLOY)
    public Future<JsonObject> undeploy(final String instanceKey) {
        return this.stub.undeploy(instanceKey);
    }
}
