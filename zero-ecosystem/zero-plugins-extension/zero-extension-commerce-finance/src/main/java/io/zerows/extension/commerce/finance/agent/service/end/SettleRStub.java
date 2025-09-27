package io.zerows.extension.commerce.finance.agent.service.end;

import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface SettleRStub {

    // Fetch Book with income and items
    Future<JsonObject> fetchSettlement(JsonArray keys);

    // Fetch Settlements by transaction id
    Future<JsonArray> fetchByTran(String transId);

    // Mount Status to settlements
    Future<JsonArray> statusSettlement(JsonArray settlements);

    Future<JsonArray> statusSettlement(List<FSettlement> settlements);
}
