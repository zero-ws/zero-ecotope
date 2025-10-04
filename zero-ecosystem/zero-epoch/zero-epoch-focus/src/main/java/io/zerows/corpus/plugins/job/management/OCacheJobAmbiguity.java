package io.zerows.corpus.plugins.job.management;

import io.zerows.corpus.plugins.job.metadata.Mission;
import io.zerows.sdk.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lang : 2024-04-20
 */
class OCacheJobAmbiguity extends AbstractAmbiguity implements OCacheJob {
    private final Set<Mission> jobs = new HashSet<>();

    OCacheJobAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<Mission> value() {
        return this.jobs;
    }

    @Override
    public OCacheJob add(final Set<Mission> missions) {
        this.jobs.addAll(missions);
        return this;
    }

    @Override
    public OCacheJob remove(final Set<Mission> missions) {
        this.jobs.removeAll(missions);
        return this;
    }
}
