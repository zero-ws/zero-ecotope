package io.zerows.extension.commerce.finance.agent.api.end;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.support.Ut;
import io.zerows.extension.commerce.finance.agent.service.end.AdjustStub;
import io.zerows.extension.commerce.finance.agent.service.end.DebtStub;
import io.zerows.extension.commerce.finance.agent.service.end.SettleWStub;
import io.zerows.extension.commerce.finance.agent.service.end.TransStub;
import io.zerows.extension.commerce.finance.eon.Addr;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-02-20
 */
@Queue
public class TransActor {

    @Inject
    private transient SettleWStub settleStub;

    @Inject
    private transient TransStub transStub;

    @Inject
    private transient DebtStub debtStub;

    @Inject
    private transient AdjustStub adjustStub;

    /**
     * 结算单已经存在的情况下的直接处理专用接口
     * <pre><code>
     *     {
     *         "settlements": [ 结算单 ],
     *         "items": [ 结算明细，finishedId 有值 ],
     *         "payment": [ 直接结算专用 ]
     *     }
     * </code></pre>
     * 此处接口执行时，结算单已经存在，所以在基础结算单环境少一个创建结算单的基本步骤，且
     * 这里的结算单处理信息是批量的。
     *
     * @param body 请求数据格式
     *
     * @return 结算单处理结果
     */
    @Address(Addr.Trans.END_TRANS)
    @Me
    public Future<JsonObject> finishAsync(final JsonObject body, final User user) {

        // PUT 方法，所以要设置创建人信息（交易创建），特殊请求属性
        Ke.umCreatedJ(body, user);

        // 1. 更新结算单
        return this.settleStub.updateAsync(body, user).compose(settlements -> {
            final JsonArray payment = Ut.valueJArray(body, FmConstant.ID.PAYMENT);
            if (Ut.isNil(payment)) {
                // 转应收
                // 2. 创建新的应收单
                return this.debtStub.createAsync(body, settlements)
                    .compose(Ux::futureJ);
            } else {
                // 直接结算
                // 2. 创建交易信息
                return this.transStub.createBySettlement(body, settlements)

                    // 3. 修正 finishedId
                    .compose(trans -> this.adjustStub.adjustAsync(trans, Ut.valueJArray(body, KName.ITEMS)))
                    .compose(Ux::futureJ);
            }
        });
    }

    @Address(Addr.Trans.END_DEBT)
    @Me
    public Future<JsonObject> debtAsync(final JsonObject body, final User user) {

        // PUT 方法，所以要设置创建人信息（交易创建），特殊请求属性
        Ke.umCreatedJ(body, user);

        // 1. 更新应收单
        return this.debtStub.updateAsync(body, user).compose(debts -> {
            // 2. 应/退款已经无后续流程，直接创建交易数据
            return this.transStub.createByDebt(body, debts)
                // 3. 修正 finishedId
                .compose(trans -> this.adjustStub.adjustAsync(trans, Ut.valueJArray(body, KName.ITEMS)))
                .compose(Ux::futureJ);
        });
    }

    @Address(Addr.Trans.FETCH_BY_KEY)
    public Future<JsonObject> fetchAsync(final String key) {
        return this.transStub.fetchAsync(key);
    }
}
