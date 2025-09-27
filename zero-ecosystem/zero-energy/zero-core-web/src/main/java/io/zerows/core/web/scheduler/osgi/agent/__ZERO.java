package io.zerows.core.web.scheduler.osgi.agent;

/**
 * @author lang : 2024-04-21
 */
interface INFO {
    String JOB_EMPTY = "Zero system detect no jobs, the scheduler will be stopped.";
    String JOB_CONFIG_NULL = "( Ignore ) Because there is no definition in `vertx-job.yml`, Job container is stop....";
    String JOB_MONITOR = "Zero system detect {0} jobs, the scheduler will begin....";
    String JOB_AGHA_SELECTED = "[ Job ] Agha = {0} has been selected for job {1} of type {2}";
    String JOB_STARTED = "[ Job ] All Job schedulers have been started!!!";
}
