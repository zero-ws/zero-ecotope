package io.zerows.extension.module.mbseapi.api;

import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * Critical routing management
 * 1. KMetadata Analyzing for zero-framework
 * 2. Call service interface of zero to findRunning all routing information
 * 3. Update routing join `RoutingCore` in zero framework
 * <p>
 * Here are futures of these uri
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Path("/api")
@Deprecated
public interface UriApi {

    @Path("routing/create")
    @POST
    Boolean createUri(@BodyParam JsonObject body);
}
