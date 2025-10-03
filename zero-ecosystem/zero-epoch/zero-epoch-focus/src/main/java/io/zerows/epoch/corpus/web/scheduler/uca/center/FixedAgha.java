package io.zerows.epoch.corpus.web.scheduler.uca.center;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.zerows.support.Ut;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.plugins.JobClient;
import io.zerows.epoch.corpus.web.scheduler.uca.timer.Interval;

class FixedAgha extends AbstractAgha {

    @Override
    public Future<Long> begin(final Mission mission) {
        // STARTING -> READY
        this.moveOn(mission, true);

        final Promise<Long> future = Promise.promise();

        final Interval interval = this.interval((timerId) ->
            JobClient.bind(timerId, mission.getCode()));

        interval.startAt((timerId) -> this.working(mission, () -> {
            /*
             * Complete future and returned Async
             */
            future.tryComplete(timerId);

            // RUNNING -> STOPPED -> READY
            Ut.itRepeat(2, () -> this.moveOn(mission, true));
        }), mission.timer());
        return future.future();
    }
}
