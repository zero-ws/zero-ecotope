package io.zerows.extension.mbse.modulat.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Off;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.mbse.modulat.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface BagArgAgent {

    // ------------------- Bag Configuration --------------------
    /*
     * Configuration of BLOCK, here will read config data of BAG
     * -- The bag is two level
     *    -- 1). When bag is root
     *    -- 2). When bag is secondary level
     * -- This method will read config data based on `bagId`
     */
    @GET
    @Path("/bag/config/:key")
    @Address(Addr.Argument.BAG_ARGUMENT)
    JsonObject fetchBag(@PathParam(KName.KEY) String bagId);

    @GET
    @Path("/bag/data/:key")
    @Address(Addr.Argument.BAG_ARGUMENT_VALUE)
    JsonObject fetchBagData(@PathParam(KName.KEY) String bagId);

    @PUT
    @Path("/bag/config/:key")
    @Address(Addr.Argument.BAG_CONFIGURE)
    @Off(address = Addr.Notify.BLOCK_CONFIGURE_UP)
    JsonObject saveBag(@PathParam(KName.KEY) String bagId,
                       @BodyParam JsonObject data);
}
