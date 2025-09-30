package io.zerows.extension.runtime.workflow.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.runtime.workflow.eon.HighWay;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface QueueAgent {

    /*
     * 1. status as condition for different
     * 2. Queue Search Results
     */
    @POST
    @Path("/up/flow-queue")
    @Address(HighWay.Queue.TASK_QUEUE)
    JsonObject fetchQueue(@BodyParam JsonObject body);

    @POST
    @Path("/up/flow-history")
    @Address(HighWay.Queue.TICKET_HISTORY)
    JsonObject fetchHistory(@BodyParam JsonObject body);

    @POST
    @Path("/up/flow-ticket")
    @Address(HighWay.Queue.TICKET_LINKAGE)
    JsonObject searchLinkage(@BodyParam JsonObject body);

    /*
     * SELECT * FROM X_FLOW by code/instanceKey
     */
    @GET
    @Path("/up/flow-definition/:code")
    @Address(HighWay.Flow.BY_CODE)
    JsonObject fetchFlow(@PathParam(KName.CODE) String code);

    /*
     * Here are two mode
     * 1. when isPre = true, the workflow is not started
     * 2. when isPre = false ( Default ), standard to pick up the task
     */
    @POST
    @Path("/up/flow-form/:pre")
    @Address(HighWay.Queue.TASK_FORM)
    JsonObject fetchForm(@BodyParam JsonObject body,
                         @PathParam("pre") Boolean isPre);

    @GET
    @Path("/up/flow/:key")
    @Address(HighWay.Flow.BY_TODO)
    JsonObject fetchTodo(@PathParam(KName.KEY) String key);

    @GET
    @Path("/up/flow-finished/:key")
    @Address(HighWay.Flow.BY_HISTORY)
    JsonObject fetchHistory(@PathParam(KName.KEY) String key);
}
