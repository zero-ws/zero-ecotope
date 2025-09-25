package io.zerows.extension.runtime.tpl.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.runtime.tpl.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

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
    JsonObject saveNotify(@PathParam(KName.KEY) String user,
                          @BodyParam JsonObject data);

    /**
     * 读取个人设置
     */
    @GET
    @Path("/my/notify/:key")
    @Address(Addr.Notify.MY_SAVE)
    JsonObject fetchNotify(@PathParam(KName.KEY) String user);
}
