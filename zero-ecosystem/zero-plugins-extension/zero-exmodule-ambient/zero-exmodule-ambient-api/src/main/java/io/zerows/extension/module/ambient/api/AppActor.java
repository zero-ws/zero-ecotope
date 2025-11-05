package io.zerows.extension.module.ambient.api;

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
import io.zerows.platform.metadata.KDS;
import io.zerows.program.Ux;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.function.Function;

@Queue
public class AppActor {

    @Inject
    private transient AppStub appStub;


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
        return this.appStub.fetchSource(appId).compose(atom -> {
            /*
             * 一个动态库
             */
            final Function<JsonObject, JsonObject> consumer = json -> {
                json.remove(KName.PASSWORD);
                json.remove(KName.USERNAME);
                return json;
            };
            /*
             * 三个静态库
             */
            final Database current = KDS.findCurrent();
            final Database history = KDS.findHistory();
            final Database workflow = KDS.findCamunda();
            final JsonObject response = new JsonObject();
            response.put("database", consumer.apply(current.toJson()));
            response.put("history", consumer.apply(history.toJson()));
            response.put("workflow", consumer.apply(workflow.toJson()));
            response.put("argument", consumer.apply(atom));
            return Ux.future(response);
        });
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
}
