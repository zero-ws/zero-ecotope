package io.zerows.extension.commerce.psi.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.commerce.psi.eon.Addr;
import jakarta.ws.rs.*;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface WhAgent {
    @POST
    @Path("/wh")
    @Address(Addr.WH_CREATE)
    JsonObject createAsync(@BodyParam JsonObject request);

    @GET
    @Path("/wh/:key")
    @Address(Addr.WH_READ)
    JsonObject readAsync(@PathParam(KName.KEY) String key);

    @DELETE
    @Path("/wh/:key")
    @Address(Addr.WH_DELETE)
    JsonObject removeAsync(@PathParam(KName.KEY) String key);

    @PUT
    @Path("/wh/:key")
    @Address(Addr.WH_UPDATE)
    JsonObject updateAsync(@PathParam(KName.KEY) String key,
                           @BodyParam JsonObject request);
}
