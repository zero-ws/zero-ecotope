package io.zerows.extension.module.finance.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.extension.module.finance.common.em.EmPay;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;

import java.util.List;

/**
 * 结算步骤
 *
 * @author lang : 2024-01-23
 */
public interface EndSettleWStub {
    Future<FSettlement> createAsync(JsonObject body, EmPay.Type type);

    /**
     * 「保留方法」
     * <pre><code>
     * 保留接口，现阶段批量结算的创建是不会出现的，只会出现单量结算功能，结算批量只有在更新过程
     * 中才会出现，简单说就是结算管理的时候多选才会出现批量结算的处理功能，而此处处理时，结算单
     * 以及结算明细都已经创建完成，只需要处理后续步骤即可。
     * </code></pre>
     */
    Future<List<FSettlement>> createAsync(JsonArray body, EmPay.Type type);

    /**
     * 更新结算单，核心逻辑如下
     * <pre><code>
     *     输入数据结构：
     *     {
     *         "settlements": [ 结算单 ],
     *         "items": [ 结算明细 ]
     *     }
     *     返回数据结构中以结算单为主，执行结算单本身的更新操作，主要用于计算
     *     结算单是否完结，如果完结则 finished = true，且提供最终结算完成
     *     时间 finishedAt，以及最终的 updatedBy 来执行结算完成步骤。
     * </code></pre>
     * 此方法只返回最新的结算单信息，不包含结算明细的更新信息。
     *
     * @param body 结算数据
     * @param user 用户信息
     * @return 结算结果
     */
    Future<List<FSettlement>> updateAsync(JsonObject body, User user);
}
