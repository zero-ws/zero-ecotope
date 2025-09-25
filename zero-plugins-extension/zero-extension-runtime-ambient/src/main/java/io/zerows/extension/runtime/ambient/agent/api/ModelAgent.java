package io.zerows.extension.runtime.ambient.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import io.zerows.extension.runtime.ambient.eon.Addr;
import jakarta.ws.rs.*;

@EndPoint
@Path("/api")
public interface ModelAgent {
    @Path("/module")
    @GET
    @Address(Addr.Module.BY_NAME)
    JsonObject moduleByName(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                            @QueryParam("entry") String entry);

    @Path("/model/fields/:identifier")
    @GET
    @Address(Addr.Module.MODEL_FIELDS)
    JsonArray modelAttributes(@PathParam(KName.IDENTIFIER) String identifier);

    @Path("/model")
    @GET
    @Address(Addr.Module.MODELS)
    JsonArray models(@HeaderParam(KWeb.HEADER.X_SIGMA) String appId);
}
