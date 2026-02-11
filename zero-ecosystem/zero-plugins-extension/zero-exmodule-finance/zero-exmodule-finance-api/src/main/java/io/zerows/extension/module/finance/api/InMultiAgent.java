package io.zerows.extension.module.finance.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 单 - 多 入账接口，这种入账模式下
 * <pre><code>
 *     1. 账单 x 1，账单明细 x N
 *        账单明细在目前版本中会包括
 *        - 商品明细
 *        - 赔偿明细
 * </code></pre>
 *
 * @author lang : 2024-01-11
 */
@EndPoint
@Path("/api")
public interface InMultiAgent {

    /**
     * 多入账模式
     * <pre><code>
     * {
     *     "orderId": "869e0b0c-4747-4c1a-b316-6c7be0122fd3",
     *     "bookId": "8c21fb10-a9c9-430d-8f50-16b87711288d",
     *     "modelId": "datum.room",
     *     "modelKey": "1105",
     *     "status": "Valid",
     *     "category": "Pay",
     *     "income": true,
     *     "indent": "NUM.PAYBILL",
     *     "type": "GoodIn",
     *     "items": [
     *         {
     *             "sigma": "ENhwBAJPZuSgIAE5EDakR6yrIQbOoOPq",
     *             "parentId": "b3016e38-d543-4699-bcde-8de470803dce",
     *             "active": true,
     *             "identifier": "hotel.commodity",
     *             "price": 100,
     *             "quantity": 2,
     *             "name": "云烟",
     *             "code": "01.0003",
     *             "helpCode": "01.0003",
     *             "payTermId": "766814e7-b5f1-46ea-b51f-f8c7efa160d1",
     *             "hotelId": "ea951ec-9e7f-403b-b437-243bfd29a4fb",
     *             "value": "210afc4e-f50d-4554-b8f6-26d1b29393d1",
     *             "label": "01.0003",
     *             "amount": 200,
     *             "language": "cn",
     *             "categoryId": "210afc4e-f50d-4554-b8f6-26d1b29393d1",
     *             "type": "hotel.commodity",
     *             "id": "78fce5a2-17f3-4dac-a75c-7e751595015c",
     *             "sort": 4,
     *             "leaf": true,
     *             "createdBy": "zero-environment",
     *             "key": "210afc4e-f50d-4554-b8f6-26d1b29393d1"
     *         },
     *         {
     *             "sigma": "ENhwBAJPZuSgIAE5EDakR6yrIQbOoOPq",
     *             "parentId": "b3016e38-d543-4699-bcde-8de470803dce",
     *             "active": true,
     *             "identifier": "hotel.commodity",
     *             "price": 100,
     *             "quantity": 1,
     *             "name": "软玉溪",
     *             "code": "01.0002",
     *             "helpCode": "01.0002",
     *             "payTermId": "ad73b0b8-56b7-425d-8b5d-4e59aa0acf8a",
     *             "hotelId": "ea951ec-9e7f-403b-b437-243bfd29a4fb",
     *             "value": "88e0d3cd-d9ca-4138-b30c-ab9e82e379a5",
     *             "label": "01.0002",
     *             "amount": 100,
     *             "language": "cn",
     *             "categoryId": "88e0d3cd-d9ca-4138-b30c-ab9e82e379a5",
     *             "type": "hotel.commodity",
     *             "id": "78fce5a2-17f3-4dac-a75c-7e751595015c",
     *             "sort": 3,
     *             "leaf": true,
     *             "createdBy": "zero-environment",
     *             "key": "88e0d3cd-d9ca-4138-b30c-ab9e82e379a5"
     *         }
     *     ]
     * }
     * </code></pre>
     *
     * @param data 输入数据
     * @return {@link JsonObject}
     */
    @POST
    @Path("/bill/multi")
    @Address(Addr.Bill.IN_MULTI)
    @OpenApi
    JsonObject inMulti(@BodyParam JsonObject data);
}
