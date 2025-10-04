package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.platform.enums.EmJob;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Job manager to manage each job here.
 */
public interface Agha {

    ConcurrentMap<EmJob.JobType, Agha> AGHAS = new ConcurrentHashMap<>() {
        {
            this.put(EmJob.JobType.FIXED, new AghaFixed());
            this.put(EmJob.JobType.ONCE, new AghaOnce());
            this.put(EmJob.JobType.FORMULA, new AghaFormula());
        }
    };

    static Agha get(final EmJob.JobType type) {
        return AGHAS.getOrDefault(type, new AghaOnce());
    }

    /**
     * Start new job by definition of Mission here.
     * Async start and return Future<Long>,
     * here long type is timerId, you can control this job by timerId
     */
    Future<Long> begin(Mission mission);
}
