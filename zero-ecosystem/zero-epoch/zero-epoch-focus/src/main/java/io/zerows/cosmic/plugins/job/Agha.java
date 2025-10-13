package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.platform.enums.EmService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Job manager to manage each job here.
 */
public interface Agha {

    ConcurrentMap<EmService.JobType, Agha> AGHAS = new ConcurrentHashMap<>() {
        {
            this.put(EmService.JobType.FIXED, new AghaFixed());
            this.put(EmService.JobType.ONCE, new AghaOnce());
            this.put(EmService.JobType.FORMULA, new AghaFormula());
        }
    };

    static Agha get(final EmService.JobType type) {
        return AGHAS.getOrDefault(type, new AghaOnce());
    }

    /**
     * Start new job by definition of Mission here.
     * Async start and return Future<Long>,
     * here long type is timerId, you can control this job by timerId
     */
    Future<Long> begin(Mission mission);
}
