package io.zerows.epoch.corpus.web.scheduler.store;

import io.zerows.epoch.annotations.Job;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.metacore.Extractor;
import io.zerows.sdk.environment.Inquirer;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JobInquirer implements Inquirer<Set<Mission>> {

    private final transient Extractor<Mission> extractor = Ut.singleton(JobExtractor.class);

    @Override
    public Set<Mission> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> jobs = clazzes.stream()
            .filter(item -> item.isAnnotationPresent(Job.class))
            .collect(Collectors.toSet());
        /* All classes of jobs here */
        this.logger().info(INFO.JOB, jobs.size());
        return jobs.stream().map(this.extractor::extract)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
