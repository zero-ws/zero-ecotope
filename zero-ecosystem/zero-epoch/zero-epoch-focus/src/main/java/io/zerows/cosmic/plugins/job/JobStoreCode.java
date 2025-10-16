package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.exception.web._501NotSupportException;
import io.zerows.cosmic.plugins.job.management.OCacheJob;
import io.zerows.cosmic.plugins.job.metadata.Mission;

import java.util.Set;

/**
 * Bridge for different JobStore
 */
class JobStoreCode implements JobStore {

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

    @Override
    public JobStore remove(final Mission mission) {
        throw new _501NotSupportException("[ ZERO ] JobStoreCode 不支持 remove 操作");
    }

    @Override
    public JobStore update(final Mission mission) {
        throw new _501NotSupportException("[ ZERO ] JobStoreCode 不支持 update 操作");
    }

    @Override
    public JobStore add(final Mission mission) {
        throw new _501NotSupportException("[ ZERO ] JobStoreCode 不支持 add 操作");
    }
}
