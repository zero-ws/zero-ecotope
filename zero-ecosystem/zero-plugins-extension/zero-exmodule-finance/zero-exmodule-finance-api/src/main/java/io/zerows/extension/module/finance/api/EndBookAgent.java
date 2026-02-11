package io.zerows.extension.module.finance.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface EndBookAgent {

    /*
     * Saving the book information when settlement join book information
     * - checked
     * - checkedDesc
     * - exceed
     * - exceedDesc
     */
    @PUT
    @Path("/settle/book/finalize")
    @Address(Addr.Settle.UP_BOOK)
    @OpenApi
    JsonObject finalizeBook(@BodyParam JsonArray books);
}
