package io.zerows.extension.commerce.finance.agent.api.end;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.commerce.finance.eon.Addr;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface BookAgent {

    /*
     * Saving the book information when settlement on book information
     * - checked
     * - checkedDesc
     * - exceed
     * - exceedDesc
     */
    @PUT
    @Path("/settle/book/finalize")
    @Address(Addr.Settle.UP_BOOK)
    JsonObject finalizeBook(@BodyParam JsonArray books);
}
