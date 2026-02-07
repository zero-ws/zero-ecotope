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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/*
 * Http Method: Get
 */
@EndPoint
@Path("/api")
@Tag(name = DescCrud.group, description = DescCrud.description)
public interface GetAgent {

    @GET
    @Path("/{actor}/{key}")
    @Address(Addr.Get.BY_ID)
    @Adjust(KWeb.ORDER.MODULE)
    JsonObject getById(@PathParam("actor") String actor,
                       @PathParam(KName.KEY) String key);

    @GET
    @Path("/{actor}/by/sigma")
    @Address(Addr.Get.BY_SIGMA)
    @Adjust(KWeb.ORDER.MODULE)
    JsonArray getAll(@PathParam("actor") String actor,
                     @QueryParam(KName.MODULE) String module);
}
