package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
@Path("/api")
public interface InstanceAgent {

    @Path("/instance/search")
    @POST
    @Address(Addr.Instance.SEARCH)
    @OpenApi
    JsonObject search(@BodyParam JsonObject criteria);

    @Path("/instance/{key}")
    @GET
    @Address(Addr.Instance.BY_ID)
    @OpenApi
    JsonObject getById(@PathParam("key") String key);

    @Path("/instance")
    @PUT
    @Address(Addr.Instance.UPSERT)
    @OpenApi
    JsonObject upsert(@BodyParam JsonObject instanceData);

    @Path("/instance/{key}/status")
    @PUT
    @Address(Addr.Instance.STATUS_UPDATE)
    @OpenApi
    JsonObject updateStatus(@PathParam("key") String key,
                            @BodyParam JsonObject body);

    @Path("/instance/{key}")
    @DELETE
    @Address(Addr.Instance.DELETE)
    @OpenApi
    JsonObject delete(@PathParam("key") String key);
}
