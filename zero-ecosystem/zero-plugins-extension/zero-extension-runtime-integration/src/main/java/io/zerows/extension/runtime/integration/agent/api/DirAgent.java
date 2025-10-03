package io.zerows.extension.runtime.integration.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.runtime.integration.eon.Addr;
import jakarta.ws.rs.*;

/**
 * Here the directory support `CRUD` on single record only, it means that
 *
 * 1. No Batch deleting processing
 * 2. But when you do modification/deleting on a directory that contains sub-dir, the system should
 * update `storePath` and rename instead
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface DirAgent {

    @POST
    @Path("/i-directory")
    @Address(Addr.Directory.ADD)
    JsonObject create(@BodyParam JsonObject body);

    @PUT
    @Path("/i-directory/:key")
    @Address(Addr.Directory.UPDATE)
    JsonObject update(@PathParam(KName.KEY) String key, @BodyParam JsonObject body);

    @DELETE
    @Path("/i-directory/:key")
    @Address(Addr.Directory.DELETE)
    JsonObject remove(@PathParam(KName.KEY) String key);
}
