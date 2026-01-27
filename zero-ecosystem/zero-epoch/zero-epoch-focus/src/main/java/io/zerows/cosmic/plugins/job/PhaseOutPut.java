package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.jigsaw.NodeStore;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
class PhaseOutPut {
    private transient final Vertx vertx;
    private transient final KRef assist = new KRef();

    PhaseOutPut(final Vertx vertx) {
        this.vertx = vertx;
    }

    PhaseOutPut bind(final KRef assist) {
        if (Objects.nonNull(assist)) {
            this.assist.add(assist.get());
        }
        return this;
    }

    Future<Envelop> outcomeAsync(final Envelop envelop, final Mission mission) {
        if (envelop.valid()) {
            /*
             * Get JobOutcome
             */
            final JobOutcome outcome = PhaseHelper.outcome(mission);
            if (Objects.isNull(outcome)) {
                /*
                 * Directly
                 */
                PhaseHelper.logOnce(mission, () ->
                    log.debug("[ ZERO ] ( Job {} ) 4. 未定义 JobOutcome，直接退出。", mission.getCode()));

                return Future.succeededFuture(envelop);
            } else {
                /*
                 * JobOutcome processing here
                 * Contract for vertx/mission
                 */
                log.debug("[ ZERO ] ( Job {} ) 2. 启用 JobOutcome 组件 {} 。", mission.getCode(), outcome.getClass().getName());
                Ut.contract(outcome, Vertx.class, this.vertx);
                Ut.contract(outcome, Mission.class, mission);

                PhaseHelper.logOnce(mission, () ->
                    log.debug("[ ZERO ] ( Job {} ) 4. --> JobOutcome 组件 {} 异步处理。", mission.getCode(), outcome.getClass().getName()));
                return outcome.afterAsync(envelop);
            }
        } else {
            PhaseHelper.logOnce(mission, () ->
                log.error("[ ZERO ] ( Job {} ) 任务出错终止，出错组件：{}", mission.getCode(), envelop.error().getClass().getName()));
            final WebException error = envelop.error();
            /*
             * For spec debug here, this code is very important
             */
            log.error(error.getMessage(), error);
            return Future.succeededFuture(envelop);
        }
    }

    Future<Envelop> outputAsync(final Envelop envelop, final Mission mission, final Vertx vertx) {
        if (envelop.valid()) {
            /*
             * Get outcome address
             */
            final String address = mission.getOutcomeAddress();
            if (Ut.isNil(address)) {
                /*
                 * Directly
                 */
                PhaseHelper.logOnce(mission, () ->
                    log.debug("[ ZERO ] ( Job {} ) 5. 未定义 Outcome Address，忽略 EventBus 直接退出。", mission.getCode()));
                return Future.succeededFuture(envelop);
            } else {
                /*
                 * Event bus provide output and then it will action
                 */

                log.debug("[ ZERO ] ( Job ) outputAsync 事件总线 EventBus 启用，地址：{}", address);
                final EventBus eventBus = this.vertx.eventBus();
                PhaseHelper.logOnce(mission, () ->
                    log.debug("[ ZERO ] ( Job {} ) 5. --> 通过事件总线地址 {} 发布输出数据。", mission.getCode(), address));

                final DeliveryOptions deliveryOptions = NodeStore.ofDelivery(vertx);
                eventBus.publish(address, envelop, deliveryOptions);
                return Future.succeededFuture(envelop);
            }
        } else {
            PhaseHelper.logOnce(mission, () ->
                log.error("[ ZERO ] ( Job {} ) 任务之前步骤出错，出错组件：{}", mission.getCode(), envelop.error().getClass().getName()));

            return Future.succeededFuture(envelop);
        }
    }
}
