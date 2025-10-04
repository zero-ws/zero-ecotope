package io.zerows.extension.mbse.action.uca.micro;

import io.zerows.corpus.plugins.job.JobStore;
import io.zerows.corpus.plugins.job.metadata.Mission;
import io.zerows.extension.mbse.action.atom.JtJob;
import io.zerows.extension.mbse.action.bootstrap.JtPin;
import io.zerows.extension.mbse.action.bootstrap.ServiceEnvironment;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 「JobStore」
 * Database job get that will be used in `vertx-jet`.
 */
public class JtHypnos implements JobStore {

    private static final ConcurrentMap<String, ServiceEnvironment> ENVS = JtPin.serviceEnvironment();

    @Override
    public JobStore remove(final Mission mission) {
        // TODO: Remove in future
        return null;
    }

    @Override
    public JobStore update(final Mission mission) {
        // TODO: Update in future
        return null;
    }

    @Override
    public JobStore add(final Mission mission) {
        // TODO: Add in future
        return null;
    }

    @Override
    public Mission fetch(final String name) {
        // TODO: Fetch in future
        return null;
    }

    @Override
    public Set<Mission> fetch() {
        return ENVS.values().stream().filter(Objects::nonNull)
            .flatMap(environment -> environment.jobs().stream())
            .map(JtJob::toJob)
            .collect(Collectors.toSet());
    }
}
