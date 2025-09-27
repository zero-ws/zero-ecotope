package io.zerows.extension.commerce.rbac.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.commerce.rbac.eon.Addr;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@EndPoint
@Path("/api")
public interface RoleActor {

    @POST
    @Path("/role")
    @Address(Addr.Role.ROLE_SAVE)
    Future<JsonObject> roleSave(@BodyParam JsonObject region);
}
