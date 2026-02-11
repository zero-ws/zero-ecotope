package io.zerows.extension.module.modulat.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

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
    @OpenApi
    JsonArray bag(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);

    /*
     * Fetch all bags that type = "EXTENSION" only, it will show
     * join the front page of `/system/setting` here.
     *
     */
    @GET
    @Path("/bag/extension")
    @Address(Addr.Module.BY_EXTENSION)
    @OpenApi
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
    @OpenApi
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
    @OpenApi
    Boolean authorize(@BodyParam JsonObject body);
}
