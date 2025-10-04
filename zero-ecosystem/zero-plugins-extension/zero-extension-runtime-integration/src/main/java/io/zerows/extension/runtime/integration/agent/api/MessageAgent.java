package io.zerows.extension.runtime.integration.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.integration.eon.Addr;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 站内信提取器
 *
 * @author lang : 2024-04-02
 */
@EndPoint
@Path("/api")
public interface MessageAgent {


    @Path("/message/type/:type")
    @GET
    @Address(Addr.Message.FETCH_TYPED)
    JsonArray fetchTyped(@PathParam(KName.TYPE) String type);

    @Path("/message/batch/:status")
    @PUT
    @Address(Addr.Message.UPDATE_STATUS)
    JsonArray readMessage(@PathParam(KName.STATUS) String status,
                          @BodyParam JsonArray keys);

    /**
     * 添加站内信，站内信存储在 I_MESSAGE 表中，数据结构如下
     * <pre><code>
     *     {
     *         "key": "主键",
     *         "name": "直接使用 message_id 记录名称",
     *         "code": "直接使用 message_id",
     *         "type": "Input, 前端传入",
     *         "status": "SENT -> 状态后续更改成 History",
     *         "subject": "Input, 前端传入",
     *         "content": "Input, 前端传入",
     *         "sendFrom": "Input, 前端传入",
     *         "sendTo": "用户ID，前端传入",
     *         "sendBy": "应用ID",
     *         "sendAt": "发送时间",
     *         "appId": "应用ID"
     *     }
     * </code></pre>
     *
     * @param data 站内信数据
     *
     * @return 站内信数据
     */
    @Path("/message")
    @POST
    @Address(Addr.Message.ADD)
    JsonObject addMessage(@BodyParam JsonObject data);


    @Path("/message/batch")
    @DELETE
    @Address(Addr.Message.DELETE_BATCH)
    JsonObject deleteMessage(@BodyParam JsonArray keys);
}
