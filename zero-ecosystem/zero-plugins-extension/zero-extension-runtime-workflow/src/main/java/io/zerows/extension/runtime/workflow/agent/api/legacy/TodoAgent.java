package io.zerows.extension.runtime.workflow.agent.api.legacy;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.extension.runtime.workflow.eon.HighWay;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@EndPoint
@Path("/api")
public interface TodoAgent {
    /*
     * Get WTodo By Id
     */
    @Path("/todo/:key")
    @GET
    @Address(HighWay.Todo.BY_ID)
    JsonObject byId(@PathParam("key") String key);
}
