package io.zerows.epoch.corpus.web.scheduler.uca.running;

import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.store.OCacheJob;

import java.util.Set;

/**
 * Bridge for different JobStore
 */
class CodeStore implements JobReader {

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
