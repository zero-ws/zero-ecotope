package io.zerows.extension.runtime.ambient.agent.api.file;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.runtime.ambient.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface FileAgent {

    @POST
    @Path("/file/my/search")
    @Address(Addr.File.MY_QUEUE)
    JsonObject searchMy(@BodyParam JsonObject query);

    @GET
    @Path("/file/my/:key")
    @Address(Addr.File.BY_KEY)
    JsonObject fileByKey(@PathParam(KName.KEY) String key);
}
