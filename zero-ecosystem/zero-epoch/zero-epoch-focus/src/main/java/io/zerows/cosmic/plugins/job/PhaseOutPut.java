package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.zerows.component.log.OLog;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;

import java.util.Objects;

class PhaseOutPut {
    private static final OLog LOGGER = Ut.Log.uca(PhaseOutPut.class);
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
            final JobOutcome outcome = PhaseElement.outcome(mission);
            if (Objects.isNull(outcome)) {
                /*
                 * Directly
                 */
                PhaseElement.onceLog(mission, () -> LOGGER.info(JobMessage.PHASE.PHASE_4TH_JOB, mission.getCode()));

                return Future.succeededFuture(envelop);
            } else {
                /*
                 * JobOutcome processing here
                 * Contract for vertx/mission
                 */
                LOGGER.info(JobMessage.PHASE.UCA_COMPONENT, "JobOutcome", outcome.getClass().getName());
                Ut.contract(outcome, Vertx.class, this.vertx);
                Ut.contract(outcome, Mission.class, mission);

                PhaseElement.onceLog(mission, () -> LOGGER.info(JobMessage.PHASE.PHASE_4TH_JOB_ASYNC, mission.getCode(), outcome.getClass().getName()));
                return outcome.afterAsync(envelop);
            }
        } else {
            PhaseElement.onceLog(mission, () -> LOGGER.info(JobMessage.PHASE.ERROR_TERMINAL, mission.getCode(), envelop.error().getClass().getName()));
            final WebException error = envelop.error();
            /*
             * For spec debug here, this code is very important
             */
            error.printStackTrace();
            return Ut.future(envelop);
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
                PhaseElement.onceLog(mission,
                    () -> LOGGER.info(JobMessage.PHASE.PHASE_5TH_JOB, mission.getCode()));
                return Future.succeededFuture(envelop);
            } else {
                /*
                 * Event bus provide output and then it will action
                 */
                LOGGER.info(JobMessage.PHASE.UCA_EVENT_BUS, "Outcome", address);
                final EventBus eventBus = this.vertx.eventBus();
                PhaseElement.onceLog(mission,
                    () -> LOGGER.info(JobMessage.PHASE.PHASE_5TH_JOB_ASYNC, mission.getCode(), address));

                final DeliveryOptions deliveryOptions = NodeStore.ofDelivery(vertx);
                eventBus.publish(address, envelop, deliveryOptions);
                return Future.succeededFuture(envelop);
            }
        } else {
            PhaseElement.onceLog(mission,
                () -> LOGGER.info(JobMessage.PHASE.ERROR_TERMINAL, mission.getCode(),
                    envelop.error().getClass().getName()));

            return Ut.future(envelop);
        }
    }
}
