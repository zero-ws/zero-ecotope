package io.zerows.extension.module.erp.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/*
 * Company Api information
 */
@EndPoint
@Path("/api")
public interface CompanyAgent {
    /*
     * /api/company/employee/:eid
     * Request: findRunning company information by user id
     */
    @GET
    @Path("company/employee/:eid")
    @Address(Addr.Company.INFORMATION)
    @OpenApi
    JsonObject company(@PathParam("eid") String eid);
}
