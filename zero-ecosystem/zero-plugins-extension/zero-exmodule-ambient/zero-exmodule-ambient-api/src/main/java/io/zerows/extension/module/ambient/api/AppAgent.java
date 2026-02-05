package io.zerows.extension.module.ambient.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
@Path("/api")
public interface AppAgent {

    /*
     * RESTful Api to Update Basic X_APP information
     */
    @Path("/app")
    @PUT
    @Address(Addr.App.UP_BY_ID)
    JsonObject updateBy(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                        @BodyParam JsonObject data);

    /*
     * Fetch all datasource
     * {
     *      "database": "",
     *      "history": "",
     *      "workflow": "",
     *      "argument": ""
     * }
     */
    @GET
    @Path("/database")
    @Address(Addr.Init.SOURCE)
    JsonObject database(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);
}
