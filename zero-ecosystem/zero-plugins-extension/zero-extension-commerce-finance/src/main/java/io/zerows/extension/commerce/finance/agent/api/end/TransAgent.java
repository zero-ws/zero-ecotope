package io.zerows.extension.commerce.finance.agent.api.end;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.commerce.finance.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author lang : 2024-02-20
 */
@EndPoint
@Path("/api")
public interface TransAgent {

    @Path("/trans-proc/standard")
    @PUT
    @Address(Addr.Trans.END_TRANS)
    JsonObject finishAsync(@BodyParam JsonObject body);

    /**
     * 请求数据
     * <pre><code>
     * {
     *     "debts": [
     *         {
     *             "amountBalance": 120.5,
     *             "sigma": "ENhwBAJPZuSgIAE5EDakR6yrIQbOoOPq",
     *             "active": true,
     *             "signName": "吕红军",
     *             "finishedAmount": 120.5,
     *             "code": "D24031112200003",
     *             "updatedAt": "2024-03-11T12:20:33",
     *             "finished": true,
     *             "updatedBy": "f7fbfaf9-8319-4eb0-9ee7-1948b8b56a67",
     *             "serial": "D24031112200003",
     *             "amount": 120.5,
     *             "language": "cn",
     *             "customerId": "6aaaef77-46d2-4faf-bdc6-2e2988b7f6bd",
     *             "type": "DEBT",
     *             "createdAt": "2024-03-11T12:20:33",
     *             "signMobile": "15922611449",
     *             "createdBy": "f7fbfaf9-8319-4eb0-9ee7-1948b8b56a67",
     *             "key": "08fc84d2-b9b6-40ce-9984-dc0165f72b90"
     *         }
     *     ],
     *     "amountActual": 121,
     *     "payment": [
     *         {
     *             "key": "Cash",
     *             "name": "Cash",
     *             "amount": 121,
     *             "language": "cn"
     *         }
     *     ],
     *     "customerName": "协议单位1",
     *     "settlements": [
     *         {
     *             "finishedAt": "2024-03-11T12:20:33",
     *             "sigma": "ENhwBAJPZuSgIAE5EDakR6yrIQbOoOPq",
     *             "relatedId": "2b5ebaf1-5553-4cba-9f3b-2ed4f69f4e4a",
     *             "active": true,
     *             "signName": "吕红军",
     *             "code": "E24031112200006",
     *             "updatedAt": "2024-03-11T12:20:33",
     *             "finished": true,
     *             "updatedBy": "f7fbfaf9-8319-4eb0-9ee7-1948b8b56a67",
     *             "serial": "E24031112200006",
     *             "amount": 121,
     *             "language": "cn",
     *             "customerId": "6aaaef77-46d2-4faf-bdc6-2e2988b7f6bd",
     *             "createdAt": "2024-03-11T12:20:33",
     *             "signMobile": "15922611449",
     *             "createdBy": "f7fbfaf9-8319-4eb0-9ee7-1948b8b56a67",
     *             "key": "38544ab8-5336-4164-9d20-1d7b2c78c52a"
     *         }
     *     ],
     *     "amountTotal": 120.5,
     *     "rounded": "HALF",
     *     "amount": 120.5,
     *     "customerId": "6aaaef77-46d2-4faf-bdc6-2e2988b7f6bd",
     *     "amountGap": "-0.50",
     *     "key": "08fc84d2-b9b6-40ce-9984-dc0165f72b90",
     *     "type": "DEBT",
     *     "language": "cn",
     *     "updatedBy": "f7fbfaf9-8319-4eb0-9ee7-1948b8b56a67",
     *     "updatedAt": "2024-03-12T02:31:37.775332Z",
     *     "sigma": "ENhwBAJPZuSgIAE5EDakR6yrIQbOoOPq",
     *     "active": true
     * }
     * </code></pre>
     *
     * @param body 请求数据
     *
     * @return 交易结果
     */
    @Path("/trans-proc/debt")
    @PUT
    @Address(Addr.Trans.END_DEBT)
    JsonObject debtAsync(@BodyParam JsonObject body);

    /**
     * 使用 Transaction 的 key 来查询本次交易信息，交易信息中主要包含以下几部分内容
     * <pre><code>
     *     {
     *         "debts": [本次交易关联的应收/应退单],
     *         "settlements": [本次交易关联的结算单],
     *         "items": [本次交易关联的结算明细],
     *         "payment": [本次交易关联的支付信息，交易的明细],
     *         "...": "...交易单的信息"
     *     }
     * </code></pre>
     * 此处的 items 到前端去计算，由于 items 和 结算单 和 应收/应退 单据是同时相关联的，所以此处不放到
     * 单独的结算单、应收/应退单中，而是独立在上层节点上，前端可直接计算，产生最终的交易单的主体内容。
     *
     * @param key 交易的唯一标识
     *
     * @return 交易信息
     */
    @Path("/trans/:key")
    @GET
    @Address(Addr.Trans.FETCH_BY_KEY)
    JsonObject fetchAsync(@PathParam(KName.KEY) String key);
}
