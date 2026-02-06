package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@EndPoint
@Path("/api")
public interface ModelAgent {
    @Path("/module")
    @GET
    @Address(Addr.Module.BY_NAME)
    @OpenApi
    JsonObject moduleByName(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                            @QueryParam("entry") String entry);

    @Path("/model/fields/:identifier")
    @GET
    @Address(Addr.Module.MODEL_FIELDS)
    @OpenApi
    JsonArray modelAttributes(@PathParam(KName.IDENTIFIER) String identifier);

    @Path("/model")
    @GET
    @Address(Addr.Module.MODELS)
    @OpenApi
    JsonArray models(@HeaderParam(KWeb.HEADER.X_SIGMA) String appId);
}
