package io.zerows.extension.module.finance.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.extension.module.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;

import java.util.List;

/**
 * @author lang : 2024-01-24
 */
public interface EndDebtStub {

    Future<FDebt> createAsync(JsonObject data, FSettlement settlement);

    Future<FDebt> createAsync(JsonObject data, List<FSettlement> settlements);

    // Fetch Debt
    Future<JsonObject> fetchDebt(JsonArray keys);

    /**
     * 更新应收单，核心逻辑如下：
     * <pre><code>
     *     输入数据结构：
     *     {
     *         "debts": [ 应收单 ],
     *     }
     *     返回数据结构以更新过的应收单为主，执行应收单本身的更新操作，根据应收单
     *     的金额计算 amountBalance 余额，以及当 amountBalance 降为 0 时，
     *     执行 finished = true 的计算。
     * </code></pre>
     *
     * @param body 应收数据
     * @param user 用户信息
     *
     * @return 应收结果
     */
    Future<List<FDebt>> updateAsync(JsonObject body, User user);
}
