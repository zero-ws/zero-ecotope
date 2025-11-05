package io.zerows.extension.module.finance.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.finance.servicespec.FetchStub;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface FetchAgent {
    /**
     * 进入账单界面的时查询结算单的专用接口，完整的查询流程：
     * 1. 根据订单 orderId 查询此订单相关的所有账单列表
     * 2. 根据账单列表查询所有的 `账单明细` 信息
     * 3. 根据账单项反向查询 `结算明细` 相关信息
     * 4. 根据结算项深度查询交易信息来完成整体的构造
     * 底层调用 {@link FetchStub} 中的方法来实现四个流程的读取整合，读取过程会有一个依赖关系
     *
     * @param orderId 订单ID
     *
     * @return {@link io.vertx.core.json.JsonObject}
     */
    @GET
    @Path("/bills/order/:orderId")
    @Address(Addr.BillItem.FETCH_AGGR)
    JsonObject fetchItem(@PathParam("orderId") String orderId);

    @GET
    @Path("/bills/:key")
    @Address(Addr.Bill.FETCH_BILL)
    JsonObject fetchByKey(@PathParam(KName.KEY) String key);

    @POST
    @Path("/bills/search/full")
    @Address(Addr.Bill.FETCH_BILLS)
    JsonObject fetchBills(@BodyParam JsonObject query);


    /*
     * Settlement to read book with authorize information
     */
    @GET
    @Path("/books/order/:orderId")
    @Address(Addr.BillItem.FETCH_BOOK)
    JsonArray fetchBooks(@PathParam("orderId") String orderId);

    /*
     * Overwrite the api
     * /api/fm-book/:key
     * instead of CRUD normalized api here
     */
    @GET
    @Path("/fm-book/:key")
    @Address(Addr.BillItem.FETCH_BOOK_BY_KEY)
    JsonObject fetchBook(@PathParam(KName.KEY) String key);
}
