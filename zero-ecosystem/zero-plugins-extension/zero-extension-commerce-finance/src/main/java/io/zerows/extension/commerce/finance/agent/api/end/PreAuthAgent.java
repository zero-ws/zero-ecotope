package io.zerows.extension.commerce.finance.agent.api.end;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.finance.eon.Addr;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.web.io.annotations.BodyParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

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
