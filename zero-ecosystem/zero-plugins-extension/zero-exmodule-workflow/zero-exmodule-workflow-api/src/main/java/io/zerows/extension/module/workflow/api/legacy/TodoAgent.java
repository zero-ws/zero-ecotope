package io.zerows.extension.module.workflow.api.legacy;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.extension.module.workflow.api.HighWay;
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
