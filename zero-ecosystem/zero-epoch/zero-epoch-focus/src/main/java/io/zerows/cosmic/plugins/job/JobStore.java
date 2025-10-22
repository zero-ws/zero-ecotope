package io.zerows.cosmic.plugins.job;

import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.Mission;

import java.util.Set;

/**
 * JobStore bridge for Set<Mission> findRunning
 * 1) @Job annotation class here
 * 2) Database job findRunning here that configured in vertx-job.yml
 */
public interface JobStore {

    default void configure(final JsonObject options) {

    }

    /*
     * Get all job definition from zero framework
     */
    Set<Mission> fetch();

    /*
     * Find job by code.
     */
    Mission fetch(String code);

    /*
     * Remove mission from findRunning
     */
    JobStore remove(Mission mission);

    /*
     * Update mission in findRunning
     */
    JobStore update(Mission mission);

    /*
     * Add new mission into Set<Mission>
     */
    JobStore add(Mission mission);
}
