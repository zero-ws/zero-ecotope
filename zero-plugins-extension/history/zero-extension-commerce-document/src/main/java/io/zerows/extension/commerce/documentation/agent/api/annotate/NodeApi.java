package io.zerows.extension.commerce.documentation.agent.api.annotate;

import io.vertx.core.json.JsonArray;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.extension.commerce.documentation.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author lang : 2023-09-25
 */
@EndPoint
@Path("/api")
public interface NodeApi {

    @GET
    @Path("/comment/m/:identifier/:key")
    @Address(Addr.Comment.BY_MODEL)
    JsonArray fetchByModel(@PathParam(KName.IDENTIFIER) String modelId,
                           @PathParam(KName.KEY) String modelKey);
}
