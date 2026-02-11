package io.zerows.extension.module.workflow.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * KRef actor part to findRunning data formatFail
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface RunAgent {
    // Testing Passed ---------------------------
    @POST
    @Path("/up/flow/start")
    @Address(HighWay.Do.FLOW_START)
    @OpenApi
    JsonObject start(@BodyParam JsonObject body);

    @PUT
    @Path("/up/flow/saving")
    @Address(HighWay.Do.FLOW_DRAFT)
    @OpenApi
    JsonObject draft(@BodyParam JsonObject body);

    // Todo for Testing -----------------------
    @PUT
    @Path("/up/flow/complete")
    @Address(HighWay.Do.FLOW_COMPLETE)
    @OpenApi
    JsonObject complete(@BodyParam JsonObject body);

    @PUT
    @Path("/up/flow/batch")
    @Address(HighWay.Do.FLOW_BATCH)
    @OpenApi
    JsonObject batch(@BodyParam JsonObject body);

    @PUT
    @Path("/up/flow/cancel")
    @Address(HighWay.Do.FLOW_CANCEL)
    @OpenApi
    JsonObject cancel(@BodyParam JsonObject body);

    @PUT
    @Path("/up/flow/close")
    @Address(HighWay.Do.FLOW_CLOSE)
    @OpenApi
    JsonObject close(@BodyParam JsonObject body);
}
