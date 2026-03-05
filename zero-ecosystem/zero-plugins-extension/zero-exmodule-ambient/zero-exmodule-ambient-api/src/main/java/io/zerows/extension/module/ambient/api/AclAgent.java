package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

@EndPoint
@Path("/api")
public interface AclAgent {

    /**
     * 菜单权限管理专用，仅限在权限管理界面使用，此接口用于提取当前应用下的所有菜单
     * <pre>
     *     appId -> 应用集合 -> Menu（菜单列表）
     * </pre>
     */
    @Path("/acl-menus")
    @GET
    @Address(Addr.Menu.ACL_FOR_MENU)
    @OpenApi
    JsonArray menuByAcl(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);
}
