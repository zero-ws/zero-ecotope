package io.zerows.extension.runtime.ambient.agent.api.linkage;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.runtime.ambient.eon.Addr;
import jakarta.ws.rs.*;

/**
 * @author lang : 2023-09-28
 */
@EndPoint
@Path("/api")
public interface TagAgent {

    @POST
    @Path("/x-tag/m")
    @Address(Addr.Tag.SYNC_SAVE)
    JsonObject saveTag(@BodyParam JsonObject body);

    @DELETE
    @Path("/x-tag/:key")
    @Address(Addr.Tag.SYNC_DELETE)
    JsonObject deleteTag(@PathParam(KName.KEY) String key);

    @GET
    @Path("/x-tag/m/:identifier/:key")
    @Address(Addr.Tag.MODEL_OF_FETCH)
    JsonObject fetchByModel(@PathParam(KName.IDENTIFIER) String modelId,
                            @PathParam(KName.KEY) String modelKey);

    @DELETE
    @Path("/x-tag/m/:identifier/:key/:tid")
    @Address(Addr.Tag.MODEL_OF_REMOVE)
    JsonObject unlinkAsync(@PathParam(KName.IDENTIFIER) String modelId,
                           @PathParam(KName.KEY) String modelKey,
                           @PathParam("tid") String tagId);

    @POST
    @Path("/x-tag/m/:identifier/:key")
    @Address(Addr.Tag.MODEL_OF_TAGS)
    JsonObject linkAsync(@PathParam(KName.IDENTIFIER) String modelId,
                         @PathParam(KName.KEY) String modelKey,
                         @BodyParam JsonArray body);
}
