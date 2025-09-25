package io.zerows.core.web.scheduler.zdk;

import io.vertx.core.Future;
import io.zerows.core.web.model.commune.Envelop;

/*
 * Job outcome, this outcome interface should provide Future<JobOut> to action
 */
public interface JobOutcome {
    /*
     * Async process outcome here
     */
    Future<Envelop> afterAsync(final Envelop envelop);
}
