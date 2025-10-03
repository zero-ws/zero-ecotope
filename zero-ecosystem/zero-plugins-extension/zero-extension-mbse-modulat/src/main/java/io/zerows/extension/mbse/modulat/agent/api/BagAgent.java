package io.zerows.extension.mbse.modulat.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.mbse.modulat.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface BagAgent {
    /*
     * Fetch all bags in current application
     */
    @GET
    @Path("/bag")
    @Address(Addr.Module.FETCH)
    JsonArray bag(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);

    /*
     * Fetch all bags that type = "EXTENSION" only, it will show
     * on the front page of `/system/setting` here.
     *
     */
    @GET
    @Path("/bag/extension")
    @Address(Addr.Module.BY_EXTENSION)
    JsonArray bagByApp(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);

    /*
     * {
     *      "key": "Block Key",
     *      "status": "DISABLED | ENABLED"
     * }
     */
    @POST
    @Path("/block/process")
    @Address(Addr.Module.UP_PROCESS)
    Boolean process(@BodyParam JsonObject body);

    /*
     * {
     *      "key": "Block Key",
     *      "license": [{
     *          "license files"
     *      }]
     * }
     */
    @POST
    @Path("/block/authorize")
    @Address(Addr.Module.UP_AUTHORIZE)
    Boolean authorize(@BodyParam JsonObject body);
}
