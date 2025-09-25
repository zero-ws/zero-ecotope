package io.zerows.extension.commerce.finance.agent.service.end;

import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTrans;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

/**
 * 修正专用服务
 * <pre><code>
 *     1. 修正 {@link FSettlementItem#getFinishedId()} 对应的值
 *        将 finishedId 关联到新创建的交易中
 *     2. 修正 {@link FSettlement#getFinished()} 对应的值
 *        根据交易的最终数据
 * </code></pre>
 *
 * @author lang : 2024-01-31
 */
public interface AdjustStub {
    /**
     * 根据结算单修正，步骤如：
     * <pre><code>
     *     这种模式只适用于直接结算模式，在直接计算模式中 {@link FSettlement} 已经设置了
     *     finished = true 的情况，此种修正提取所有的 {@link FSettlementItem} 并设置
     *     对应的 finishedId = trans {@link FTrans} 的主键。
     * </code></pre>
     * 注意此处修正的是结算明细的 finishedId，而且结算明细来自于 {@link FSettlement} 表
     * 的读取：WHERE SETTLEMENT_ID = settlement.getKey() 的方式查询底层数据对象，并且
     * 给出对应的明细提取。
     *
     * @param trans      交易记录
     * @param settlement 结算单
     *
     * @return 修正后的交易记录
     */
    Future<FTrans> adjustAsync(FTrans trans, FSettlement settlement);
    
    Future<FTrans> adjustAsync(FTrans trans, JsonArray items);
}
