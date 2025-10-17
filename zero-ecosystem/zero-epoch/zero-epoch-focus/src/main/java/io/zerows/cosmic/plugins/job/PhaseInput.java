package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.zerows.component.log.LogO;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
class PhaseInput {

    private static final LogO LOGGER = Ut.Log.uca(PhaseInput.class);

    private transient final Vertx vertx;
    private transient final KRef underway = new KRef();

    PhaseInput(final Vertx vertx) {
        this.vertx = vertx;
    }

    KRef underway() {
        return this.underway;
    }

    Future<Envelop> inputAsync(final Mission mission) {
        /*
         * Get income address
         * 1) If there configured income address, it means that there are some inputs came from
         *     'incomeAddress' ( For feature usage )
         * 2) No incomeAddress configured is often used for the job.
         * */
        final String address = mission.getIncomeAddress();
        if (Ut.isNil(address)) {
            /*
             * Event bus did not provide any input here
             */
            PhaseHelper.logOnce(mission, () ->
                log.info("[ ZERO ] ( Job {} ) 1. 输入 JsonObject 类型的数据。", mission.getCode()));

            return Future.succeededFuture(Envelop.okJson());
        } else {
            /*
             * Event bus provide input and then it will pass to @On
             */
            log.info("[ ZERO ] ( Job ) inputAsync 事件总线 EventBus 启用，地址：{}", address);
            final Promise<Envelop> input = Promise.promise();
            final EventBus eventBus = this.vertx.eventBus();
            eventBus.<Envelop>consumer(address, handler -> {

                PhaseHelper.logOnce(mission, () ->
                    log.info("[ ZERO ] ( Job {} ) 1. 从事件总线地址 {} 上接收到输入数据。", mission.getCode(), address));

                final Envelop envelop = handler.body();
                if (Objects.isNull(envelop)) {
                    /*
                     * Success
                     */
                    input.complete(Envelop.ok());
                } else {
                    /*
                     * Failure
                     */
                    input.complete(envelop);
                }
            }).completion().onComplete(item -> {
                /*
                 * This handler will cause finally for future
                 * If no data came from address
                 */
                final Object result = item.result();
                if (Objects.isNull(result)) {
                    input.complete(Envelop.ok());
                } else {
                    input.complete(Envelop.success(result));
                }
            });
            return input.future();
        }
    }

    Future<Envelop> incomeAsync(final Envelop envelop, final Mission mission) {
        /*
         * Get JobIncome
         */
        final JobIncome income = PhaseHelper.income(mission);
        if (envelop.valid()) {
            if (Objects.isNull(income)) {
                /*
                 * Directly
                 */
                PhaseHelper.logOnce(mission, () ->
                    log.info("[ ZERO ] ( Job {} ) 2. 无需 JobIncome 定义，直接跳过。", mission.getCode()));
                return Future.succeededFuture(envelop);
            } else {
                /*
                 * JobIncome processing here
                 * Contract for vertx/mission
                 */
                log.info("[ ZERO ] ( Job {} ) 2. 启用 JobIncome 组件 {} 。", mission.getCode(), income.getClass().getName());
                /*
                 * JobIncome must define
                 * - Vertx reference
                 * - Mission reference
                 */
                Ut.contract(income, Vertx.class, this.vertx);
                Ut.contract(income, Mission.class, mission);
                /*
                 * Here we could calculate directory
                 */
                PhaseHelper.logOnce(mission, () ->
                    log.info("[ ZERO ] ( Job {} ) 2. 开始执行 JobIncome 组件 {} 。", mission.getCode(), income.getClass().getName()));

                return income.underway().compose(refer -> {
                    /*
                     * Here provide extension for JobIncome
                     * 1 - You can do some operations in JobIncome to calculate underway data such as
                     *     dictionary data here.
                     * 2 - Also you can put some assist data into `KRef`, this `KRef` will be used
                     *     by major code logical instead of `re-calculate` the data again.
                     * 3 - For performance design, this structure could be chain passed in:
                     *     KIncome -> Job ( Channel ) -> Outcome
                     *
                     * Critical:  It's only supported by `Actor/Job` structure instead of `Api` passive
                     *     mode in Http Request / Response. it means that Api could not support this feature.
                     */
                    this.underway.add(refer.get());
                    return income.beforeAsync(envelop);
                });
            }
        } else {
            PhaseHelper.logOnce(mission, () ->
                log.info("[ ZERO ] ( Job {} ) 任务出错终止，出错组件：{}", mission.getCode(), income.getClass().getName()));
            return Ut.future(envelop);
        }
    }
}
