package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
@Path("/api")
public interface RoleActor {

    @POST
    @Path("/role")
    @Address(Addr.Role.ROLE_SAVE)
    Future<JsonObject> roleSave(@BodyParam JsonObject region);
}
