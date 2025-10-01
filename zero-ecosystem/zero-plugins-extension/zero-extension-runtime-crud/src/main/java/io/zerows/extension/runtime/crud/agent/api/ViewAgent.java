package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.based.constant.KWeb;
import io.zerows.epoch.corpus.io.annotations.PointParam;
import io.zerows.epoch.corpus.metadata.commune.Vis;
import io.zerows.extension.runtime.crud.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/*
 * Http Method: Get
 */
@EndPoint
@Path("/api")
public interface ViewAgent {
    @GET
    @Path("/columns/{actor}/full")
    @Address(Addr.Get.COLUMN_FULL)
    @Adjust(KWeb.ORDER.MODULE)
    JsonArray getFull(@PathParam(KName.ACTOR) String actor,
                      @QueryParam(KName.MODULE) String module,
                      @PointParam(KName.VIEW) Vis view);

    @GET
    @Path("/columns/{actor}/my")
    @Address(Addr.Get.COLUMN_MY)
    @Adjust(KWeb.ORDER.MODULE)
    JsonArray getMy(@PathParam(KName.ACTOR) String actor,
                    @QueryParam(KName.MODULE) String module,
                    @PointParam(KName.VIEW) Vis view);
}
