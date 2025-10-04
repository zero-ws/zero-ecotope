package io.zerows.corpus.plugins.job;

import io.vertx.core.Future;
import io.zerows.epoch.web.Envelop;

/*
 * Job outcome, this outcome interface should provide Future<JobOut> to action
 */
public interface JobOutcome {
    /*
     * Async process outcome here
     */
    Future<Envelop> afterAsync(final Envelop envelop);
}
