package io.zerows.epoch.corpus.web.scheduler.uca.center;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.atom.specification.KScheduler;
import io.zerows.epoch.corpus.web.scheduler.uca.timer.Interval;
import io.zerows.epoch.enums.EmJob;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class FormulaAgha extends AbstractAgha {
    @Override
    public Future<Long> begin(final Mission mission) {
        // STARTING -> READY
        this.moveOn(mission, true);

        return this.execute(mission);
    }

    private Future<Long> execute(final Mission mission) {
        final Promise<Long> promise = Promise.promise();
        final Interval interval = this.interval();
        final KScheduler timer = mission.timer();
        interval.restartAt((timeId) -> {
            // STOPPED -> READY
            if (EmJob.Status.STOPPED == mission.getStatus()) {
                this.moveOn(mission, true);
            }
            this.working(mission, () -> {
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
