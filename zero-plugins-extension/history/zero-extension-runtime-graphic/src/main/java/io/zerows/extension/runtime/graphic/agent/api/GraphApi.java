package io.zerows.extension.runtime.graphic.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.extension.runtime.graphic.eon.Addr;
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
