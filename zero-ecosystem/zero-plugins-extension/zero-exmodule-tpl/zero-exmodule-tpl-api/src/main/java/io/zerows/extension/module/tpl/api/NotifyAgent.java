package io.zerows.extension.module.tpl.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 保存当前设置，包括个人设置 / 以及角色设置
 * 现阶段主要针对提醒
 *
 * @author lang : 2024-04-02
 */
@EndPoint
@Path("/api")
public interface NotifyAgent {
    /**
     * 保存个人设置
     */
    @PUT
    @Path("/my/notify/:key")
    @Address(Addr.Notify.MY_FETCH)
    @OpenApi
    JsonObject saveNotify(@PathParam(KName.KEY) String user,
                          @BodyParam JsonObject data);

    /**
     * 读取个人设置
     */
    @GET
    @Path("/my/notify/:key")
    @Address(Addr.Notify.MY_SAVE)
    @OpenApi
    JsonObject fetchNotify(@PathParam(KName.KEY) String user);
}
