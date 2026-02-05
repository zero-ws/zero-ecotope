package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface FileAgent {

    @POST
    @Path("/file/my/search")
    @Address(Addr.File.MY_QUEUE)
    @OpenApi
    JsonObject searchMy(@BodyParam JsonObject query);

    @GET
    @Path("/file/my/:key")
    @Address(Addr.File.BY_KEY)
    @OpenApi
    JsonObject fileByKey(@PathParam(KName.KEY) String key);
}
