package io.zerows.extension.crud.api;

import io.r2mo.openapi.operations.DescCrud;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.KView;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.PointParam;

/*
 * HTTP Method: Put
 */
@EndPoint
@Path("/api")
@Tag(name = DescCrud.group, description = DescCrud.description)
public interface PutAgent {
    @PUT
    @Path("/{actor}/{key}")
    @Address(Addr.Put.BY_ID)
    @Adjust(KWeb.ORDER.MODULE)
    JsonObject update(@PathParam("actor") String actor,
                      @PathParam(KName.KEY) String key,
                      @BodyParam JsonObject data);

    @PUT
    @Path("/batch/{actor}/update")
    @Address(Addr.Put.BATCH)
    @Adjust(KWeb.ORDER.MODULE)
    JsonArray updateBatch(@PathParam("actor") String actor,
                          @BodyParam JsonArray dataArray,
                          @QueryParam(KName.MODULE) String module);

    @PUT
    @Path("/columns/{actor}/my")
    @Address(Addr.Put.COLUMN_MY)
    @Adjust(KWeb.ORDER.MODULE)
    JsonArray getMy(@PathParam("actor") String actor,
                    @BodyParam JsonObject viewData,
                    @QueryParam(KName.MODULE) String module,
                    @PointParam(KName.VIEW) KView view);
}
