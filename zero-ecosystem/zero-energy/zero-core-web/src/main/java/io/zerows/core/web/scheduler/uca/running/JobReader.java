package io.zerows.core.web.scheduler.uca.running;

import io.zerows.core.web.scheduler.atom.Mission;

import java.util.Set;

/*
 * Package using here for job reading
 */
interface JobReader {

    /*
     * Get all job definition from zero framework
     */
    Set<Mission> fetch();

    /*
     * Find job by code.
     */
    Mission fetch(String code);
}
