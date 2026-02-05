package io.zerows.extension.crud.api;

import io.r2mo.openapi.operations.DescCrud;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.KView;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.PointParam;

/*
 * HTTP Method: POST
 */
@EndPoint
@Path("/api")
@Tag(name = DescCrud.group, description = DescCrud.description)
public interface PostAgent {
    /*
     * Pure Creating for different entity
     */
    @POST
    @Path("/{actor}")
    @Address(Addr.Post.ADD)
    @Adjust(KWeb.ORDER.MODULE)
    JsonObject create(@PathParam("actor") String actor,
                      @BodyParam JsonObject data);

    /*
     * Search Interface for JqTool Engine
     */
    @POST
    @Path("/{actor}/search")
    @Address(Addr.Post.SEARCH)
    @Adjust(KWeb.ORDER.MODULE)
    JsonObject search(@PathParam("actor") String actor,
                      @BodyParam JsonObject data,
                      @QueryParam(KName.MODULE) String module,
                      @PointParam(KName.VIEW) KView view);

    /*
     * Existing/Missing Interface for Async Validation
     */
    @POST
    @Path("/{actor}/existing")
    @Address(Addr.Post.EXISTING)
    @Adjust(KWeb.ORDER.MODULE)
    Boolean existing(@PathParam("actor") String actor,
                     @BodyParam JsonObject criteria,
                     @QueryParam(KName.MODULE) String module);

    @POST
    @Path("/{actor}/missing")
    @Address(Addr.Post.MISSING)
    @Adjust(KWeb.ORDER.MODULE)
    Boolean missing(@PathParam("actor") String actor,
                    @BodyParam JsonObject criteria,
                    @QueryParam(KName.MODULE) String module);
}
