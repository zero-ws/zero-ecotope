package io.zerows.corpus.plugins.job;

import io.zerows.corpus.plugins.job.management.OCacheJob;
import io.zerows.corpus.plugins.job.metadata.Mission;

import java.util.Set;

/**
 * Bridge for different JobStore
 */
class StoreCode implements JobReader {

    @Override
    public Set<Mission> fetch() {
        return OCacheJob.entireValue();
    }

    @Override
    public Mission fetch(final String code) {
        return this.fetch().stream()
            .filter(mission -> code.equals(mission.getCode()))
            .findFirst().orElse(null);
    }
}
