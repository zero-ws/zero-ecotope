package io.zerows.extension.module.finance.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 预授权解锁处理
 *
 * @author lang : 2024-01-11
 */
@EndPoint
@Path("/api")
public interface EndPreAuthAgent {

    /*
     * Unlock Authorize when settlement join `status`
     * From `Pending` to `Finished`
     */
    @PUT
    @Path("/settle/authorize/unlock")
    @Address(Addr.Settle.UNLOCK_AUTHORIZE)
    JsonObject unlockAuthorize(@BodyParam JsonArray authorize);
}
