package io.zerows.extension.commerce.finance.uca.trans;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.commerce.finance.domain.tables.daos.FSettlementDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 这是更新步骤，可以直接根据主键更新 {@link FSettlement} 来完成字段更新和执行。
 * <pre><code>
 *     输入
 *     {
 *         "key": "xxxx"
 *     }
 *     更新字段
 *     - finished
 *     - finishedAt
 *     - updatedBy
 *     - updatedAt
 * </code></pre>
 *
 * @author lang : 2024-01-22
 */
class Sync01Settlement implements Trade<User, FSettlement> {
    @Override
    public Future<FSettlement> flatter(final JsonObject data, final User user) {
        final String key = Ut.valueString(data, KName.KEY);
        Objects.requireNonNull(key);
        final ADB jq = DB.on(FSettlementDao.class);
        return jq.<FSettlement>fetchByIdAsync(key)
            // 更新 Settlement
            .compose(settlement -> {
                this.executeFinished(settlement, user);
                return jq.updateAsync(settlement);
            });
    }

    @Override
    public Future<List<FSettlement>> scatter(final JsonArray data, final User assist) {
        final ADB jq = DB.on(FSettlementDao.class);
        final Set<String> keys = Ut.valueSetString(data, KName.KEY);
        return jq.<FSettlement>fetchInAsync(KName.KEY, Ut.toJArray(keys))
            // 更新 Settlement
            .compose(settlements -> {
                settlements.forEach(settlement -> this.executeFinished(settlement, assist));
                return jq.updateAsync(settlements);
            });
    }

    private void executeFinished(final FSettlement settlement, final User user) {
        if (Objects.isNull(settlement)) {
            return;
        }
        final LocalDateTime nowAt = LocalDateTime.now();
        settlement.setFinished(Boolean.TRUE);
        settlement.setFinishedAt(nowAt);
        settlement.setUpdatedBy(Ux.keyUser(user));
        settlement.setUpdatedAt(nowAt);
    }
}
