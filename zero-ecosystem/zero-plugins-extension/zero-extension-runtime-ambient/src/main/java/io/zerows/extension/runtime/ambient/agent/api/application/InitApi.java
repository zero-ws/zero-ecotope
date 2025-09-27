package io.zerows.extension.runtime.ambient.agent.api.application;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KWeb;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.runtime.ambient.eon.Addr;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@EndPoint
@Path("/api")
public interface InitApi {

    @Path("/app/init")
    @POST
    @Address(Addr.Init.INIT)
    JsonObject init(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                    @BodyParam JsonObject body);

    @Path("/app/prepare/{name}")
    @POST
    @Address(Addr.Init.PREPARE)
    JsonObject prepare(@PathParam("name") String name);

    @Path("/app/connect")
    @POST
    @Address(Addr.Init.CONNECT)
    String connect(@BodyParam JsonObject body);
}
