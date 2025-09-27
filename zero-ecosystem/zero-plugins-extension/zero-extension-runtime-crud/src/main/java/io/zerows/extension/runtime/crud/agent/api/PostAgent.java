package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Adjust;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.core.web.io.annotations.PointParam;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.module.domain.atom.commune.Vis;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/*
 * HTTP Method: POST
 */
@EndPoint
@Path("/api")
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
                      @PointParam(KName.VIEW) Vis view);

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
