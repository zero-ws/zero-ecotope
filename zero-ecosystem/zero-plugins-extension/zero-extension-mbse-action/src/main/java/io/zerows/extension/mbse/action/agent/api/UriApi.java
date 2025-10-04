package io.zerows.extension.mbse.action.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.extension.mbse.action.eon.JtAddr;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * Critical routing management
 * 1. KMetadata Analyzing for zero-framework
 * 2. Call service interface of zero to get all routing information
 * 3. Update routing on `RoutingCore` in zero framework
 *
 * Here are futures of these uri
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface UriApi {

    @Path("routing/create")
    @POST
    @Address(JtAddr.Aeon.NEW_ROUTE)
    Boolean createUri(@BodyParam JsonObject body);
}
