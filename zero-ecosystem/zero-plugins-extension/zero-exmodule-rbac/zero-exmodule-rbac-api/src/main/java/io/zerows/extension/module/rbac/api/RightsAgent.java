package io.zerows.extension.module.rbac.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

@EndPoint
public interface RightsAgent {

    @Path("/api/groups")
    @GET
    @Address(Addr.Group.GROUP_SIGMA)
    JsonObject fetchGroups(@HeaderParam(KWeb.HEADER.X_SIGMA) String sigma);
}
