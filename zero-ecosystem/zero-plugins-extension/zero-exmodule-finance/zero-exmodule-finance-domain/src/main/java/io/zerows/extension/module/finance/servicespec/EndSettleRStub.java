package io.zerows.extension.module.finance.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;

import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface EndSettleRStub {

    // Fetch Book with income and items
    Future<JsonObject> fetchSettlement(JsonArray keys);

    // Fetch Settlements by transaction id
    Future<JsonArray> fetchByTran(String transId);

    // Mount Status to settlements
    Future<JsonArray> statusSettlement(JsonArray settlements);

    Future<JsonArray> statusSettlement(List<FSettlement> settlements);
}
