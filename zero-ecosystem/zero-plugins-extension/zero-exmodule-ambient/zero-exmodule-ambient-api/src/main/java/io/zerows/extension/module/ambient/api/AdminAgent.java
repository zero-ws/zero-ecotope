package io.zerows.extension.module.ambient.api;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

/**
 * @author lang : 2024-07-26
 */
@EndPoint
@Path("/api")
public interface AdminAgent {

    /**
     * 读取当前租户可用的所有应用列表
     * <pre><code>
     *     WHERE TENANT_ID = ?
     * </code></pre>
     * 租户的 X_APP 在购买时通过 AppStore 直接加载和安装到环境中，从配置文件到数据库
     * <pre><code>
     *     1. 配置文件中预设当前平台中所有的应用列表
     *     2. X_APP 则保存了当前租户可用的所有应用列表
     *     3. 若是租户级别的管理员账号，那么可直接在工作台通过菜单应用管理来调整当前租户的所有应用信息
     *        status 调整
     * </code></pre>
     *
     * @param tenantId 租户ID
     * @return 应用列表
     */
    @Path("/apps/usable")
    @GET
    @Address(Addr.App.ADMIN_USABLE)
    JsonArray fetchUsable(
        @HeaderParam(KWeb.HEADER.X_TENANT_ID) String tenantId);
}
