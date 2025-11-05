package io.zerows.extension.module.finance.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Off;
import io.zerows.epoch.constant.KName;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 拆、冲、转
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface InVarietyAgent {
    /**
     * 拆账
     *
     * @param key  被拆的账单明细记录
     * @param data 拆账的数据
     *
     * @return 拆账结果
     */
    @PUT
    @Path("/bill-item/split/:key")
    @Address(Addr.BillItem.UP_SPLIT)
    JsonObject upSplit(@PathParam(KName.KEY) String key, @BodyParam JsonObject data);

    /**
     * 冲账
     *
     * @param key  被冲账的账单明细记录
     * @param data 冲账的数据
     *
     * @return 冲账结果
     */
    @PUT
    @Path("/bill-item/revert/:key")
    @Address(Addr.BillItem.UP_REVERT)
    @Off(address = Addr.Notify.REVERSAL_ORDER)
    JsonObject upRevert(@PathParam(KName.KEY) String key, @BodyParam JsonObject data);

    /**
     * 转账
     *
     * @param bookId 账本ID
     * @param data   转账数据
     *
     * @return 转账结果
     */
    @PUT
    @Path("/bill-item/transfer/:key")
    @Address(Addr.Bill.UP_TRANSFER)
    JsonObject upTransfer(@PathParam(KName.KEY) String bookId, @BodyParam JsonObject data);
}
