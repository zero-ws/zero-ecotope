package io.zerows.extension.commerce.finance.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.extension.commerce.finance.eon.Addr;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * @author lang : 2024-01-25
 */
@EndPoint
@Path("/api")
public interface BatchAgent {

    @POST
    @Path("/settlement/batch")
    @Address(Addr.Settle.FETCH_BY_KEY)
    JsonObject fetchSettlement(@BodyParam JsonArray keys);

    @POST
    @Path("/settlement/search")
    @Address(Addr.Settle.FETCH_BY_QR)
    JsonObject searchSettles(@BodyParam JsonObject qr);

    @POST
    @Path("/debt/batch")
    @Address(Addr.Settle.FETCH_DEBT)
    JsonObject fetchDebt(@BodyParam JsonArray keys);
}
