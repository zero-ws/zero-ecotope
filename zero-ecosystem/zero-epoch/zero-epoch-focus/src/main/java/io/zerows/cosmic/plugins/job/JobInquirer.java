package io.zerows.cosmic.plugins.job;

import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Job;
import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JobInquirer implements Inquirer<Set<Mission>> {

    public static final String JOB = "( {0} Job ) The Zero system has found " +
        "{0} components of @Job.";
    private final transient Extractor<Mission> extractor = Ut.singleton(JobExtractor.class);

    @Override
    public Set<Mission> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> jobs = clazzes.stream()
            .filter(item -> item.isAnnotationPresent(Job.class))
            .collect(Collectors.toSet());
        /* All classes of jobs here */
        this.logger().info(JOB, jobs.size());
        return jobs.stream().map(this.extractor::extract)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
