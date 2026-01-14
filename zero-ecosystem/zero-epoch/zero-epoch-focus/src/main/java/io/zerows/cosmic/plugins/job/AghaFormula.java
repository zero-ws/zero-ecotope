package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.zerows.cosmic.plugins.job.metadata.KScheduler;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.platform.enums.EmService;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AghaFormula extends AghaAbstract {
    @Override
    public Future<Long> begin(final Mission mission) {
        // STARTING -> READY
        this.moveOn(mission, true);

        return this.execute(mission);
    }

    private Future<Long> execute(final Mission mission) {
        final Promise<Long> promise = Promise.promise();
        final JobInterval interval = this.interval();
        final KScheduler timer = mission.scheduler();
        interval.restartAt((timeId) -> {
            // STOPPED -> READY
            if (EmService.JobStatus.STOPPED == mission.getStatus()) {
                this.moveOn(mission, true);
            }
            this.working(mission.timerId(timeId), () -> {
                /*
                 * Complete future and returned Async
                 */
                promise.tryComplete(timeId);

                // RUNNING -> STOPPED
                this.moveOn(mission, true);
            });
        }, timer);
        return promise.future()
            /*
             * Call internal action in loop because of
             * continue working on
             */
            .compose(finished -> this.execute(mission));
    }
}
