package io.zerows.extension.module.report.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.report.common.RGeneration;
import io.zerows.extension.module.report.domain.tables.pojos.KpReport;

/**
 * @author lang : 2024-10-08
 */
public interface ReportStub {

    Future<JsonArray> fetchReports(String appId);

    Future<JsonObject> buildInstance(String reportId, JsonObject params);

    Future<RGeneration> buildGeneration(KpReport report, JsonObject params);

    Future<JsonArray> buildData(KpReport report, JsonObject params);
}
