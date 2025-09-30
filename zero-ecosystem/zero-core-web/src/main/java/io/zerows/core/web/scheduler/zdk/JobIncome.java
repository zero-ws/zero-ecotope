package io.zerows.core.web.scheduler.zdk;

import io.vertx.core.Future;
import io.zerows.common.program.KRef;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.commune.Envelop;

/*
 * Job income before, this income interface should provide Future<JobIn> to Job to consume
 */
public interface JobIncome {
    /*
     * Async process income here
     */
    Future<Envelop> beforeAsync(final Envelop envelop);

    /*
     * Hidden channel to pass dict data,
     * It's underway data passing from
     * KIncome -> Job -> Outcome
     */
    default Future<KRef> underway() {
        final KRef refer = new KRef();
        return Ut.future(refer);
    }
}
