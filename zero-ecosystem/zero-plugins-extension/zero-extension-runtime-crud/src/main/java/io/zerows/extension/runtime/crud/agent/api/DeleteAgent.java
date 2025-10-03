package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.runtime.crud.eon.Addr;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/*
 * Http Method: DELETE
 */
@EndPoint
@Path("/api")
public interface DeleteAgent {
    @DELETE
    @Path("/{actor}/{key}")
    @Address(Addr.Delete.BY_ID)
    @Adjust(KWeb.ORDER.MODULE)
    Boolean delete(@PathParam("actor") String actor,
                   @PathParam(KName.KEY) String key);

    @DELETE
    @Path("/batch/{actor}/delete")
    @Address(Addr.Delete.BATCH)
    @Adjust(KWeb.ORDER.MODULE)
    Boolean deleteBatch(@PathParam("actor") String actor,
                        @BodyParam JsonArray data,
                        @QueryParam(KName.MODULE) String module);
}
