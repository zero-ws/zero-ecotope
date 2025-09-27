package io.zerows.core.web.scheduler.uca.running;

import io.zerows.core.web.scheduler.atom.Mission;

/**
 * JobStore bridge for Set<Mission> get
 * 1) @Job annotation class here
 * 2) Database job get here that configured in vertx-job.yml
 */
public interface JobStore extends JobReader {

    /*
     * Remove mission from get
     */
    JobStore remove(Mission mission);

    /*
     * Update mission in get
     */
    JobStore update(Mission mission);

    /*
     * Add new mission into Set<Mission>
     */
    JobStore add(Mission mission);
}
