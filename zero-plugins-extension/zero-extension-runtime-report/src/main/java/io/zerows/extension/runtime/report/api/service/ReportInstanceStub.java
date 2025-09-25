package io.zerows.extension.runtime.report.api.service;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;

/**
 * @author lang : 2024-10-11
 */
public interface ReportInstanceStub {

    Future<JsonObject> searchPaged(JsonObject query);

    Future<Buffer> exportAsync(String key);

    Future<KpReportInstance> buildAsync(JsonArray data, JsonObject params,
                                        RGeneration generation);

    Future<JsonObject> saveInstance(String key, JsonObject data);

    Future<Boolean> deleteInstance(String key);

    Future<JsonObject> fetchInstance(String key);
}
