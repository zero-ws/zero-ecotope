package io.zerows.extension.module.report.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/**
 * @author lang : 2024-10-08
 */
@EndPoint
@Path("/api")
public interface ReportAgent {
    /**
     * 读取所有报表（定义），整个报表的生命周期中
     * <pre><code>
     *     1. 定义报表（依赖设计器）
     *     2. 生成实例（根据筛选条件直接生成对应实例）
     *     3. 读取实例（报表呈现）+ 导出
     * </code></pre>
     *
     * @param appId 应用ID
     * @return 报表清单
     */
    @GET
    @Path("/report/query-all")
    @Address(Addr.Report.QUERY_ALL)
    @OpenApi
    Future<JsonArray> fetchAllReports(@HeaderParam("X-App-Id") String appId);

    /**
     * 此处接口是读取所有的报表实例，所以并没有追加额外的条件，简单说读取过程中要求前端
     * 传递相关信息，限制条件
     * <pre>
     *     如果 criteria 中什么内容都没有的话，就不读取任何数据，简单说必须传入最少一个
     *     过滤条件，针对当前的 App 应用进行读取。
     * </pre>
     *
     * @param query 查询条件
     * @return 报表实例
     */
    @POST
    @Path("/report/query-page")
    @Address(Addr.Report.QUERY_PAGE)
    @OpenApi
    Future<JsonObject> instancePaged(JsonObject query);

    @POST
    @Path("/report/single-generate")
    @Address(Addr.Report.SINGLE_GENERATE)
    @OpenApi
    Future<JsonObject> instanceGenerate(
        @QueryParam("reportId") String reportId, JsonObject query);

    @PUT
    @Path("/report/single-instance/:key")
    @Address(Addr.Report.SINGLE_SAVE)
    @OpenApi
    Future<JsonObject> instanceSave(
        @PathParam(KName.KEY) String instanceId, JsonObject data);

    @DELETE
    @Path("/report/single-instance/:key")
    @Address(Addr.Report.SINGLE_DELETE)
    @OpenApi
    Future<Boolean> instanceDelete(
        @PathParam(KName.KEY) String instanceId);

    @GET
    @Path("/report/single-instance/:key")
    @Address(Addr.Report.SINGLE_FETCH)
    @OpenApi
    Future<JsonObject> instanceFetch(
        @PathParam(KName.KEY) String instanceId);

    @POST
    @Path("/report/single-export/:key")
    @Address(Addr.Report.SINGLE_EXPORT)
    @OpenApi
    Future<Buffer> instanceExport(
        @PathParam(KName.KEY) String instanceId);
}
