package io.zerows.epoch.corpus.web.scheduler.store;

/**
 * @author lang : 2024-04-19
 */
interface INFO {
    String JOB = "( {0} Job ) The Zero system has found " +
        "{0} components of @Job.";
    String JOB_IGNORE = "[ Job ] The class {0} annotated with @Job will be ignored because there is no @On method defined.";
}
