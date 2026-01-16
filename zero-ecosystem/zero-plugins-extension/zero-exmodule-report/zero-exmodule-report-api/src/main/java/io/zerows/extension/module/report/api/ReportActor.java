package io.zerows.extension.module.report.api;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.em.EmReport;
import io.zerows.extension.module.report.exception._80700Exception400QueryParameter;
import io.zerows.extension.module.report.exception._80701Exception404ReportMissing;
import io.zerows.extension.module.report.servicespec.ReportInstanceStub;
import io.zerows.extension.module.report.servicespec.ReportStub;
import io.zerows.platform.constant.VName;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.time.Instant;

/**
 * @author lang : 2024-10-08
 */
@Queue
public class ReportActor {
    @Inject
    private ReportStub reportStub;

    @Inject
    private ReportInstanceStub instanceStub;

    @Address(Addr.Report.QUERY_ALL)
    public Future<JsonArray> fetchAllReports(final String appId) {
        return this.reportStub.fetchReports(appId);
    }

    @Address(Addr.Report.SINGLE_GENERATE)
    public Future<JsonObject> instanceGenerate(final String reportId,
                                               final JsonObject query,
                                               final User user) {
        if (Ut.isNil(reportId)) {
            // ERR-80701
            return FnVertx.failOut(_80701Exception404ReportMissing.class, reportId);
        }
        final String userKey = Ux.userId(user);
        query.put(KName.USER, userKey);
        return this.reportStub.buildInstance(reportId, query);
    }

    @Address(Addr.Report.SINGLE_SAVE)
    public Future<JsonObject> instanceSave(final String key,
                                           final JsonObject data,
                                           final User user) {
        final JsonObject saveData = data.copy();
        saveData.put(KName.CREATED_BY, Ux.userId(user));
        saveData.put(KName.CREATED_AT, Instant.now());
        saveData.put(KName.ACTIVE, Boolean.TRUE);
        saveData.put(KName.STATUS, EmReport.UcaStatus.ACTIVE.name());
        return this.instanceStub.saveInstance(key, saveData);
    }


    @Address(Addr.Report.SINGLE_DELETE)
    public Future<Boolean> instanceDelete(final String key) {
        return this.instanceStub.deleteInstance(key);
    }

    @Address(Addr.Report.SINGLE_FETCH)
    public Future<JsonObject> instanceFetch(final String key) {
        return this.instanceStub.fetchInstance(key);
    }

    @Address(Addr.Report.QUERY_PAGE)
    public Future<JsonObject> instancePaged(final JsonObject query) {
        final JsonObject criteriaJ = Ut.valueJObject(query, VName.KEY_CRITERIA);
        if (Ut.isNil(criteriaJ)) {
            // ERR-80700
            return FnVertx.failOut(_80700Exception400QueryParameter.class, query.encode());
        }
        return this.instanceStub.searchPaged(query);
    }

    @Address(Addr.Report.SINGLE_EXPORT)
    public Future<Buffer> instanceExport(final String instanceId) {
        return this.instanceStub.exportAsync(instanceId);
    }
}
