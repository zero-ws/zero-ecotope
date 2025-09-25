package io.zerows.extension.commerce.erp.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.commerce.erp.eon.Addr;
import jakarta.ws.rs.*;

/*
 * Employee update here: Employee information ADD / DELETE / UPDATE
 * 1) ADD
 * -- 1.1）If `userId` set, call Nexus to update `modelKey` and `modelId`
 * -- 1.2）Otherwise add employee record directly.
 * 2）UPDATE
 * -- `userId` changed double check
 * -- 2.1）Remove, update original S_USER to set null
 * -- 2.2）Add, update the latest S_USER `modelKey` and `modelId`
 * -- 2.3）Change, update original S_USER to set null / update the latest S_USER
 * 3) DELETE
 * -- Update original S_USER to set null
 */
@EndPoint
@Path("/api")
public interface EmployeeAgent {
    /*
     * Get by id finished,
     * append `userId` that related to S_USER account here.
     */
    @GET
    @Path("employee/:key")
    @Address(Addr.Employee.BY_ID)
    JsonObject fetchEmployee(@PathParam("key") String key);

    @POST
    @Path("employee")
    @Address(Addr.Employee.ADD)
    JsonObject add(@BodyParam JsonObject params);

    @PUT
    @Path("employee/:key")
    @Address(Addr.Employee.EDIT)
    JsonObject update(@PathParam("key") String key, @BodyParam JsonObject params);

    @DELETE
    @Path("employee/:key")
    @Address(Addr.Employee.DELETE)
    JsonObject remove(@PathParam("key") String key);
}
