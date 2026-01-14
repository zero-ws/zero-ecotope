package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.support.Ut;

class AghaFixed extends AghaAbstract {

    @Override
    public Future<Long> begin(final Mission mission) {
        final Promise<Long> handler = Promise.promise();
        // 提取延迟执行逻辑
        // STARTING -> READY
        this.moveOn(mission, true);

        final JobInterval interval = this.interval((timerId) ->
            JobClient.bind(timerId, mission.getCode()));

        interval.startAt((timerId) ->
            this.working(mission, () -> {
                /*
                 * Complete future and returned Async
                 */
                handler.tryComplete(timerId);

                // RUNNING -> STOPPED -> READY
                Ut.itRepeat(2, () -> this.moveOn(mission, true));
            }), mission.scheduler()
        );

        return handler.future();
    }
}
