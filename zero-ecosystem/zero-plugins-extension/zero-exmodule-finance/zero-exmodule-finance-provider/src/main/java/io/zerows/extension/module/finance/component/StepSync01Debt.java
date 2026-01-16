package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.finance.domain.tables.daos.FDebtDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FDebt;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-03-12
 */
class StepSync01Debt implements Step<User, FDebt> {
    @Override
    public Future<FDebt> flatter(final JsonObject data, final User user) {
        final String key = Ut.valueString(data, KName.KEY);
        Objects.requireNonNull(key);
        final ADB jq = DB.on(FDebtDao.class);
        return jq.<FDebt>fetchByIdAsync(key)
            // 更新 Debt
            .compose(debt -> {
                this.executeFinished(debt, data, user);
                return jq.updateAsync(debt);
            });
    }

    @Override
    public Future<List<FDebt>> scatter(final JsonArray data, final User user) {
        final ADB jq = DB.on(FDebtDao.class);
        final Set<String> keys = Ut.valueSetString(data, KName.KEY);
        return jq.<FDebt>fetchInAsync(KName.KEY, Ut.toJArray(keys))
            // 更新 Debt
            .compose(debts -> {
                final ConcurrentMap<String, JsonObject> dataMap = Ut.elementMap(data, KName.KEY);

                debts.forEach(debt -> {
                    final JsonObject dataJ = dataMap.get(debt.getKey());
                    if (Ut.isNotNil(dataJ)) {
                        this.executeFinished(debt, dataJ, user);
                    }
                });
                return jq.updateAsync(debts);
            });
    }

    private void executeFinished(final FDebt debt, final JsonObject dataJ, final User user) {
        // updatedBy / updatedAt
        // finished / finishedAt
        final LocalDateTime nowAt = LocalDateTime.now();
        final String userKey = Ux.userId(user);

        /*
         * finished = true
         * amountBalance = amount - finishedAmount
         */
        final BigDecimal amount = debt.getAmountBalance();
        final BigDecimal amountFinished = new BigDecimal(Ut.valueString(dataJ, "finishedAmount"));
        final BigDecimal amountBalance = amount.subtract(amountFinished);

        debt.setAmountBalance(amountBalance);

        if (0 == amountBalance.doubleValue()) {
            debt.setFinished(Boolean.TRUE);
            debt.setFinishedAt(nowAt);
        }

        debt.setUpdatedBy(userKey);
        debt.setUpdatedAt(nowAt);
    }
}
