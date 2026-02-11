package io.zerows.extension.module.finance.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 单 - 单 入账接口，这种入账模式分如下两种
 * <pre><code>
 *     1. 账单 x 1, 账单明细 x 1, 预授权 x 1         POST /api/income/pre
 *     2. 账单 x 1, 账单明细 x 1                    POST /api/income/single
 * </code></pre>
 * 此处带预授权的入账和不带预授权的入账是做过分流的，即访问不同的接口来执行
 *
 * @author lang : 2024-01-11
 */
@EndPoint
@Path("/api")
public interface InSingleAgent {
    /**
     * 带预授权模式的押金入账
     * <pre><code>
     * {
     *      "key": "入账时候前端不提供账单主键",
     *      "name": "和选择的账单项有关，对应账单项的名称",
     *      "code/serial": "序号信息根据 indent 提供的值进行计算，访问 X_NUMBER 表生成单号",
     *      "type": "Pre 账单类型",
     *      "category": "Pay 账单类别",
     *      "amount": "账单金额",
     *      "income": "true - 消费项，false - 付款项",
     *      "comment": "前端录入",
     *      "orderId": "前端关联订单ID",
     *      "bookId": "前端关联账本ID",
     *      "manualNo": "录入手工单号",
     *      "payTermId": "账单项关联ID",
     *      "indent": "NUM.PAYBILL（默认值），提供序号生成的 X_NUMBER 中的 CODE 对应",
     *      "preAuthorize": {
     *           "code/serial": "系统计算",
     *           "amount": "预授权金额信息",
     *           "comment": "预授权备注",
     *           "expiredAt": "预授超时信息",
     *           "bankName": "银行名称",
     *           "bankCard": "银行卡号",
     *           "orderId": "关联订单ID",
     *           "billId": "关联账单ID"
     *      }
     * }
     * </code></pre>
     * 这个方法会处理三张表
     * <pre><code>
     *     - F_BILL / F_BILL_ITEM 账单和账单明细
     *     - F_PRE_AUTHORIZE 预授权表
     * </code></pre>
     */
    @POST
    @Path("/bill/pre")
    @Address(Addr.Bill.IN_PRE)
    @OpenApi
    JsonObject inPre(@BodyParam JsonObject data);

    /**
     * 不带预授权模式的普通入账
     * <pre><code>
     *     {
     *          "key": "入账的时候不提供账单主键",
     *          "name": "入账的账单标题",
     *          "code/serial": "序号信息根据 indent 提供的值进行计算，访问 X_NUMBER 表生成单号",
     *          "type": "Pre 账单类型",
     *          "category": "Pay 账单类别",
     *          "amount": "账单金额",
     *          "income": "true - 消费项，false - 付款项",
     *          "comment": "前端录入",
     *          "orderId": "前端关联订单ID",
     *          "bookId": "前端关联账本ID",
     *          "manualNo": "录入手工单号",
     *          "payTermId": "账单项关联ID",
     *          "indent": "NUM.PAYBILL（默认值），提供序号生成的 X_NUMBER 中的 CODE 对应",
     *     }
     * </code></pre>
     *
     * @param data {@link JsonObject}
     * @return {@link JsonObject}
     */
    @POST
    @Path("/bill/single")
    @Address(Addr.Bill.IN_COMMON)
    @OpenApi
    JsonObject inCommon(@BodyParam JsonObject data);
}
