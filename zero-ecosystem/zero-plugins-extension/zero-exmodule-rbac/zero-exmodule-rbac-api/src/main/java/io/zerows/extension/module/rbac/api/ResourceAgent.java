package io.zerows.extension.module.rbac.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface ResourceAgent {

    @Path("/resource/search")
    @POST
    @Address(Addr.Authority.RESOURCE_SEARCH)
    @OpenApi
    JsonArray searchAsync(@BodyParam JsonObject query);

    @Path("/resource/{key}")
    @GET
    @Address(Addr.Authority.RESOURCE_GET_CASCADE)
    @OpenApi
    JsonObject getResourceById(@PathParam("key") String key);

    @Path("/resource")
    @POST
    @Address(Addr.Authority.RESOURCE_ADD_CASCADE)
    @OpenApi
    JsonObject addResource(@BodyParam JsonObject data);

    @Path("/resource/{key}")
    @PUT
    @Address(Addr.Authority.RESOURCE_UPDATE_CASCADE)
    @OpenApi
    JsonObject updateResourceById(@PathParam("key") String key,
                                  @BodyParam JsonObject data);

    @Path("resource/{key}")
    @DELETE
    @Address(Addr.Authority.RESOURCE_DELETE_CASCADE)
    @OpenApi
    Boolean deleteResourceById(@PathParam("key") String key);
}
