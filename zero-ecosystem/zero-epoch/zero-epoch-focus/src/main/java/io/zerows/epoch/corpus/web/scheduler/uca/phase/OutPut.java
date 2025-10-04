package io.zerows.epoch.corpus.web.scheduler.uca.phase;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.zerows.component.log.OLog;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.eon.MessageOfJob;
import io.zerows.epoch.corpus.web.scheduler.zdk.JobOutcome;
import io.zerows.epoch.management.OCacheNode;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;

import java.util.Objects;

class OutPut {
    private static final OLog LOGGER = Ut.Log.uca(OutPut.class);
    private transient final Vertx vertx;
    private transient final KRef assist = new KRef();

    OutPut(final Vertx vertx) {
        this.vertx = vertx;
    }

    OutPut bind(final KRef assist) {
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
            final JobOutcome outcome = Element.outcome(mission);
            if (Objects.isNull(outcome)) {
                /*
                 * Directly
                 */
                Element.onceLog(mission, () -> LOGGER.info(MessageOfJob.PHASE.PHASE_4TH_JOB, mission.getCode()));

                return Future.succeededFuture(envelop);
            } else {
                /*
                 * JobOutcome processing here
                 * Contract for vertx/mission
                 */
                LOGGER.info(MessageOfJob.PHASE.UCA_COMPONENT, "JobOutcome", outcome.getClass().getName());
                Ut.contract(outcome, Vertx.class, this.vertx);
                Ut.contract(outcome, Mission.class, mission);

                Element.onceLog(mission, () -> LOGGER.info(MessageOfJob.PHASE.PHASE_4TH_JOB_ASYNC, mission.getCode(), outcome.getClass().getName()));
                return outcome.afterAsync(envelop);
            }
        } else {
            Element.onceLog(mission, () -> LOGGER.info(MessageOfJob.PHASE.ERROR_TERMINAL, mission.getCode(), envelop.error().getClass().getName()));
            final WebException error = envelop.error();
            /*
             * For spec debug here, this code is very important
             */
            error.printStackTrace();
            return Ut.future(envelop);
        }
    }

    Future<Envelop> outputAsync(final Envelop envelop, final Mission mission) {
        if (envelop.valid()) {
            /*
             * Get outcome address
             */
            final String address = mission.getOutcomeAddress();
            if (Ut.isNil(address)) {
                /*
                 * Directly
                 */
                Element.onceLog(mission,
                    () -> LOGGER.info(MessageOfJob.PHASE.PHASE_5TH_JOB, mission.getCode()));
                return Future.succeededFuture(envelop);
            } else {
                /*
                 * Event bus provide output and then it will action
                 */
                LOGGER.info(MessageOfJob.PHASE.UCA_EVENT_BUS, "Outcome", address);
                final EventBus eventBus = this.vertx.eventBus();
                Element.onceLog(mission,
                    () -> LOGGER.info(MessageOfJob.PHASE.PHASE_5TH_JOB_ASYNC, mission.getCode(), address));

                final NodeNetwork network = OCacheNode.of().network();
                final DeliveryOptions deliveryOptions = network.get().optionDelivery();
                eventBus.publish(address, envelop, deliveryOptions);
                return Future.succeededFuture(envelop);
            }
        } else {
            Element.onceLog(mission,
                () -> LOGGER.info(MessageOfJob.PHASE.ERROR_TERMINAL, mission.getCode(),
                    envelop.error().getClass().getName()));

            return Ut.future(envelop);
        }
    }
}
