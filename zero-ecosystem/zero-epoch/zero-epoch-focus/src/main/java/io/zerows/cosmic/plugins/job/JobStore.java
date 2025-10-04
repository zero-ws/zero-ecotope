package io.zerows.cosmic.plugins.job;

import io.zerows.cosmic.plugins.job.metadata.Mission;

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
