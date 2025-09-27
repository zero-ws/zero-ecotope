package io.zerows.extension.commerce.documentation.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.commerce.documentation.eon.Addr;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * @author lang : 2023-09-14
 */
@EndPoint
@Path("/api")
public interface TokenApi {

    @Path("/doc/token")
    @POST
    @Address(Addr.TOKEN_REQUEST)
    JsonObject tokenRequest(@BodyParam JsonObject body);
}
