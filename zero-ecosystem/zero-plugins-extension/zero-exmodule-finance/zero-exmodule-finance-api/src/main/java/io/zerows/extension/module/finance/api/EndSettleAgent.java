package io.zerows.extension.module.finance.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 生命周期概述
 * <pre><code>
 *     1. 结算单
 *        现结：finished = true，其他：finished = false
 *     2. 结算明细
 *        不转应收：debtId = null, 转应收：debtId 有值
 *     3. 应收单类型
 *        应收：type = DEBT, 应退：type = REFUND
 *        已处理：finished = true, 未处理：finished = false
 *     4. 交易完成的单：finished = true
 *     5. 交易单类型
 *        直接结算：type = SETTLEMENT
 *        针对应收交易：type = DEBT
 *        针对应退交易：type = REFUND
 * </code></pre>
 *
 * @author lang : 2024-01-17
 */
@EndPoint
@Path("/api")
public interface EndSettleAgent {
    /**
     * （单）直接结算
     * <pre><code>
     *     结算单 x 1
     *     结算明细 x N
     *     交易关联 x 1
     *     交易单 x 1
     *     交易明细（根据 payment 计算）
     * </code></pre>
     *
     * @param body 传入的数据
     * @return 结算结果
     */
    @Path("/trans/direct")
    @POST
    @Address(Addr.Trans.START_DIRECT)
    @OpenApi
    JsonObject directAsync(@BodyParam JsonObject body);

    /**
     * （单）延迟结算
     * <pre><code>
     *     结算单 x 1
     *     结算明细 x N
     * </code></pre>
     *
     * @param body 传入的数据
     * @return 结算结果
     */
    @Path("/trans/delay")
    @POST
    @Address(Addr.Trans.START_DELAY)
    @OpenApi
    JsonObject delayAsync(@BodyParam JsonObject body);


    /**
     * （单）直接转应收
     * <pre><code>
     *     结算单 x 1
     *     结算明细 x N
     *     应收单 x 1
     * </code></pre>
     *
     * @param body 传入的数据
     * @return 结算结果
     */
    @Path("/trans/debt")
    @POST
    @Address(Addr.Trans.START_DEBT)
    @OpenApi
    JsonObject debtAsync(@BodyParam JsonObject body);
}
