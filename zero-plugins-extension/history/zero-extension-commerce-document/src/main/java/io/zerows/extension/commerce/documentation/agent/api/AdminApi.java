package io.zerows.extension.commerce.documentation.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.commerce.documentation.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * 法规管理界面专用接口
 * <pre><code>
 *     1. 保存专用接口
 *        stored = false 时，新增这条数据
 *        D_DOC_CLAUSE / D_REFER
 *        stored = true 时，更新这条数据
 *        D_DOC_CLAUSE
 *     2. 读取专用接口，直接提供 D_DOC 主键提取旗下所有条款
 * </code></pre>
 *
 * @author lang : 2023-09-24
 */
@EndPoint
@Path("/api")
public interface AdminApi {
    /**
     * 批量保存，请求的数据格式如下：
     * <pre><code>
     *     {
     *         "data": [],
     *         "record": {}
     *     }
     *     上述结构中：
     *     - data：待保存和计算的数据，对应到 D_DOC_CLAUSE
     *     - record：待保存的主记录，主记录可能对应 D_DOC / D_DOC_SEGMENT
     * </code></pre>
     *
     * @param body 请求数据
     *
     * @return 保存的最终结果
     */
    @PUT
    @Path("/doc-clause/saving")
    @Address(Addr.Clause.SAVE)
    JsonArray saveDoc(@BodyParam JsonObject body);

    @GET
    @Path("/doc-clause/by-doc/:key")
    @Address(Addr.Clause.BY_DOC)
    JsonArray fetchClause(@PathParam(KName.KEY) String docId);
}
