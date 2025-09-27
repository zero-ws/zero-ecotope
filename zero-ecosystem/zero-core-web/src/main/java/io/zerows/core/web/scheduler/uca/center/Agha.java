package io.zerows.core.web.scheduler.uca.center;

import io.vertx.core.Future;
import io.zerows.core.constant.em.EmJob;
import io.zerows.core.web.scheduler.atom.Mission;

/**
 * Job manager to manage each job here.
 */
public interface Agha {

    static Agha get(final EmJob.JobType type) {
        return CACHE.AGHAS.getOrDefault(type, new OnceAgha());
    }

    /**
     * Start new job by definition of Mission here.
     * Async start and return Future<Long>,
     * here long type is timerId, you can control this job by timerId
     */
    Future<Long> begin(Mission mission);
}
