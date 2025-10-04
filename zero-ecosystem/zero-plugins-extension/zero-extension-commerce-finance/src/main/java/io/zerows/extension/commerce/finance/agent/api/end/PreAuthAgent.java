package io.zerows.extension.commerce.finance.agent.api.end;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.extension.commerce.finance.eon.Addr;
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
public interface PreAuthAgent {

    /*
     * Unlock Authorize when settlement on `status`
     * From `Pending` to `Finished`
     */
    @PUT
    @Path("/settle/authorize/unlock")
    @Address(Addr.Settle.UNLOCK_AUTHORIZE)
    JsonObject unlockAuthorize(@BodyParam JsonArray authorize);
}
