package io.zerows.extension.module.graphic.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@EndPoint
@Path("/api")
public interface GraphApi {

    @Path("/graphic/analyze/:key")
    @GET
    @Address(Addr.GRAPH_ANALYZE)
    JsonObject searchGraph(@PathParam("key") String key,
                           @QueryParam("graph") String graph,
                           @QueryParam("level") Integer level);

}
