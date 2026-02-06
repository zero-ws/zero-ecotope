package io.zerows.extension.module.rbac.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

@Path("/api")
@EndPoint
public interface ViewMyAgent {

    /*
     * View Add, DELETE, FETCH
     * 1. Add ( Provide name, title, projection )
     * 2. Delete ( By key )
     * 3. Fetch ( method, uri, sigma, type )
     */
    @POST
    @Path("/view-p/fetch")
    @Address(Addr.View.VIEW_P_BY_USER)
    @OpenApi
    JsonArray pViewByUser(@BodyParam JsonObject params);

    @POST
    @Path("/view-p/existing")
    @Address(Addr.View.VIEW_P_EXISTING)
    @OpenApi
    Boolean pViewExisting(@BodyParam JsonObject params);

    @DELETE
    @Path("/view-p/:key")
    @Address(Addr.View.VIEW_P_DELETE)
    @OpenApi
    Boolean pViewDelete(@PathParam(KName.KEY) String key);

    @PUT
    @Path("/view-p/:key")
    @Address(Addr.View.VIEW_P_UPDATE)
    @OpenApi
    Boolean pViewById(@PathParam(KName.KEY) String key,
                      @BodyParam JsonObject params);

    @GET
    @Path("/view-p/:key")
    @Address(Addr.View.VIEW_P_BY_ID)
    @OpenApi
    Boolean pViewUpdate(@PathParam(KName.KEY) String key);


    @DELETE
    @Path("/batch/view-p/delete")
    @OpenApi
    @Address(Addr.View.VIEW_P_BATCH_DELETE)
    Boolean pViewsDelete(@BodyParam JsonArray keys);

    @POST
    @Path("/view-p")
    @Address(Addr.View.VIEW_P_ADD)
    @OpenApi
    Boolean pViewCreate(@BodyParam JsonObject params);
}
