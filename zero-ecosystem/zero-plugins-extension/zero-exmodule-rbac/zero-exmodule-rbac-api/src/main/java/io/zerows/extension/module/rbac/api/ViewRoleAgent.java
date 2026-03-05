package io.zerows.extension.module.rbac.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.module.rbac.domain.tables.pojos.SView;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.PointParam;

@EndPoint
@Path("/api")
public interface ViewRoleAgent {
    /**
     * 根据资源查询角色权限
     * <pre>
     *    1. name / position ( 视图专用数据 )
     *    2. ownerType = ROLE, roleId = :role
     *    3. resourceId = :res
     * </pre>
     *
     * @return View 视图数据
     */
    @Path("/acl/role-view/{owner}/{res}")
    @GET
    @Address(Addr.View.VIEW_R_GET)
    @OpenApi
    JsonObject viewByRole(@PathParam("owner") String owner,
                          @PathParam("res") String resourceId,
                          @PointParam(KName.VIEW) KView view);

    @Path("/acl/role-view/{owner}/{res}")
    @PUT
    @Address(Addr.View.VIEW_R_SAVE)
    @OpenApi
    JsonObject viewSync(@PathParam("owner") String owner,
                        @PathParam("res") String resourceId,
                        @BodyParam SView myView,
                        @PointParam(KName.VIEW) KView view);
}
