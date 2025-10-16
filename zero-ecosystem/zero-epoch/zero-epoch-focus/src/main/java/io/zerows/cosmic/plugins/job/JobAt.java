package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.enums.typed.PerMode;

import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * Calculation for the next runAt to update the timer
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface JobAt {

    Cc<String, JobAt> CC_JOB_AT = Cc.openThread();
    ConcurrentMap<PerMode, Supplier<JobAt>> AT_MAP = new ConcurrentHashMap<>() {
        {
            this.put(PerMode.D, JobAtDaily::new);
            this.put(PerMode.W, JobAtWeekly::new);
            this.put(PerMode.M, JobAtMonthly::new);
            this.put(PerMode.Q, JobAtQuarterly::new);
            this.put(PerMode.Y, JobAtYearly::new);
        }
    };

    static JobAt instance(final PerMode mode) {
        return CC_JOB_AT.pick(() -> {
            final Supplier<JobAt> jobAt = AT_MAP.get(mode);
            return jobAt.get();
        }, mode.name());
    }

    default Queue<Instant> analyze(final List<String> formula) {
        return this.analyze(formula, Instant.now());
    }

    Queue<Instant> analyze(List<String> formula, Instant started);

    String format();
}
