package io.zerows.extension.commerce.documentation.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.extension.commerce.documentation.eon.Addr;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Document Server 专用下载链接，可以让 ONLYOFFICE 直接从此下载链接下载
 * 远程文件并产生编辑的效果，此处有几个限制：
 * 1. 只可以使用 fileKey 下载文件，其他内容的下载模式不支持
 * 2. 此接口是公共的（不带安全认证，Document Server不可以执行反向认证）
 * 3. zero-doc 才开放这种接口，保证基本安全性，token 值会使用 zero-rbac 做强验证
 *
 * @author lang : 2023-09-15
 */
@EndPoint
public class PullAgent {
    @Path("/doc/get/{fileKey}")
    @GET
    @Address(Addr.DOC_DOWNLOAD)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public JsonObject download(@PathParam("fileKey") final String key,
                               @QueryParam("token") final String token) {
        return new JsonObject()
            .put(KName.KEY, key)
            .put(KName.TOKEN, token);
    }
}
