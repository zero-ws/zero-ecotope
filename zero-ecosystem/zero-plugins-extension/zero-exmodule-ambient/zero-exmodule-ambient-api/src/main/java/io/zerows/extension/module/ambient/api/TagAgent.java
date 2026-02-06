package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * @author lang : 2023-09-28
 */
@EndPoint
@Path("/api")
public interface TagAgent {

    @POST
    @Path("/x-tag/m")
    @Address(Addr.Tag.SYNC_SAVE)
    @OpenApi
    JsonObject saveTag(@BodyParam JsonObject body);

    @DELETE
    @Path("/x-tag/:key")
    @Address(Addr.Tag.SYNC_DELETE)
    @OpenApi
    JsonObject deleteTag(@PathParam(KName.KEY) String key);

    @GET
    @Path("/x-tag/m/:identifier/:key")
    @Address(Addr.Tag.MODEL_OF_FETCH)
    @OpenApi
    JsonObject fetchByModel(@PathParam(KName.IDENTIFIER) String modelId,
                            @PathParam(KName.KEY) String modelKey);

    @DELETE
    @Path("/x-tag/m/:identifier/:key/:tid")
    @Address(Addr.Tag.MODEL_OF_REMOVE)
    @OpenApi
    JsonObject unlinkAsync(@PathParam(KName.IDENTIFIER) String modelId,
                           @PathParam(KName.KEY) String modelKey,
                           @PathParam("tid") String tagId);

    @POST
    @Path("/x-tag/m/:identifier/:key")
    @Address(Addr.Tag.MODEL_OF_TAGS)
    @OpenApi
    JsonObject linkAsync(@PathParam(KName.IDENTIFIER) String modelId,
                         @PathParam(KName.KEY) String modelKey,
                         @BodyParam JsonArray body);
}
