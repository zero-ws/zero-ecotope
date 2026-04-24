package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
    @OpenApi
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
    @OpenApi
    JsonObject database(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);

    @Path("/app/init")
    @POST
    @Address(Addr.Init.INIT)
    @OpenApi
    JsonObject init(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                    @BodyParam JsonObject body);

    @Path("/app/prepare/{name}")
    @POST
    @Address(Addr.Init.PREPARE)
    @OpenApi
    JsonObject prepare(@PathParam("name") String name);

    @Path("/app/deploy")
    @POST
    @Address(Addr.Init.DEPLOY)
    @OpenApi
    JsonObject deploy(@BodyParam JsonObject body);

    @Path("/instance/deploy")
    @POST
    @Address(Addr.Init.DEPLOY)
    @OpenApi
    JsonObject deployCompat(@BodyParam JsonObject body);

    @Path("/app/health-check/{key}")
    @GET
    @Address(Addr.Init.HEALTH_CHECK)
    @OpenApi
    JsonObject healthCheck(@PathParam("key") String instanceKey);

    @Path("/instance/{key}/health")
    @GET
    @Address(Addr.Init.HEALTH_CHECK)
    @OpenApi
    JsonObject healthCheckCompat(@PathParam("key") String instanceKey);

    @Path("/app/undeploy/{key}")
    @DELETE
    @Address(Addr.Init.UNDEPLOY)
    @OpenApi
    JsonObject undeploy(@PathParam("key") String instanceKey);

    @Path("/instance/{key}/undeploy")
    @POST
    @Address(Addr.Init.UNDEPLOY)
    @OpenApi
    JsonObject undeployCompat(@PathParam("key") String instanceKey);

    @Path("/instance/{key}/undeploy")
    @DELETE
    @Address(Addr.Init.UNDEPLOY)
    @OpenApi
    JsonObject undeployCompatDelete(@PathParam("key") String instanceKey);
}
