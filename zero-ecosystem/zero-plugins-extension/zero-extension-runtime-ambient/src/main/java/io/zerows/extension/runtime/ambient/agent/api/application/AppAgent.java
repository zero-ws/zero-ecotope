package io.zerows.extension.runtime.ambient.agent.api.application;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.runtime.ambient.eon.Addr;
import jakarta.ws.rs.*;

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

    /*
     * 1. Step 1: Update the Notice by `expiredAt` first
     * 2. Step 2: Query the valid `notice` records from the system
     */
    @POST
    @Path("/notice-dashboard")
    @Address(Addr.Init.NOTICE)
    JsonArray notice(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                     @BodyParam JsonObject condition);
}
