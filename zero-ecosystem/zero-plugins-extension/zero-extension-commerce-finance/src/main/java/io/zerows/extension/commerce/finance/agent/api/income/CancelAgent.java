package io.zerows.extension.commerce.finance.agent.api.income;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.commerce.finance.eon.Addr;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 撤销入账明细的专用方法，根据不同的类型执行相关撤销
 *
 * @author lang : 2024-01-11
 */
@EndPoint
@Path("/api")
public interface CancelAgent {
    /**
     * 撤销入账明细专员方法
     * <pre><code>
     *     关于 type
     *     - item：撤销所有类型的入账
     *     - divide：撤销拆账信息
     * </code></pre>
     *
     * @param type 撤销类型
     * @param data 基本请求数据
     *
     * @return 撤销结果
     */
    @PUT
    @Path("/bill-item/cancel/:type")
    @Address(Addr.BillItem.UP_CANCEL)
    JsonObject upCancel(@PathParam(KName.TYPE) String type, @BodyParam JsonObject data);
}
