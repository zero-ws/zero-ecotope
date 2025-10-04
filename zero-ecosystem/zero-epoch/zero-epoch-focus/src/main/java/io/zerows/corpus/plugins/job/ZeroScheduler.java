package io.zerows.corpus.plugins.job;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.environment.DevEnv;
import io.zerows.component.log.OLog;
import io.zerows.corpus.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Worker;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmJob;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Background worker of Zero framework, it's for schedule of background tasks here.
 * This scheduler is for task deployment, it should deploy all tasks
 * This worker must be SINGLE ( instances = 1 ) because multi worker with the same tasks may be
 * conflicts
 */
@Worker(instances = VValue.SINGLE)
public class ZeroScheduler extends AbstractVerticle {

    private static final String JOB_EMPTY = "Zero system detect no jobs, the scheduler will be stopped.";
    private static final String JOB_CONFIG_NULL = "( Ignore ) Because there is no definition in `vertx-job.yml`, Job container is stop....";
    private static final String JOB_MONITOR = "Zero system detect {0} jobs, the scheduler will begin....";
    private static final String JOB_AGHA_SELECTED = "[ Job ] Agha = {0} has been selected for job {1} of type {2}";
    private static final String JOB_STARTED = "[ Job ] All Job schedulers have been started!!!";
    private static final OLog LOGGER = Ut.Log.vertx(ZeroScheduler.class);
    private static final JobStore STORE = JobPin.getStore();

    public ZeroScheduler() {
    }

    @Override
    public void start() {
        /* Whether contains JobConfig? */
        final JobConfig config = JobPin.getConfig();
        if (Objects.nonNull(config)) {
            /* Pick Up all Mission definition from system */
            final Set<Mission> missions = STORE.fetch();
            /* Whether there exist Mission definition */
            if (missions.isEmpty()) {
                LOGGER.info(JOB_EMPTY);
            } else {
                LOGGER.info(JOB_MONITOR, missions.size());
                /* Start each job here by different types */
                final List<Future<Void>> futures = new ArrayList<>();
                missions.forEach(mission -> futures.add(this.start(mission)));
                FnBase.combineT(futures).onSuccess(nil -> LOGGER.info(JOB_STARTED));
            }
        } else {
            LOGGER.info(JOB_CONFIG_NULL);
        }
    }

    private Future<Void> start(final Mission mission) {
        /*
         * Prepare for mission, it's verf important to bind mission object to Vertx
         * instead of bind(Vertx) method.
         */
        final Object reference = mission.getProxy();
        if (Objects.nonNull(reference)) {
            /*
             * Bind vertx
             */
            Ut.contract(reference, Vertx.class, this.vertx);
        }
        /*
         * Agha calling
         */
        final Agha agha = Agha.get(mission.getType());
        if (Objects.nonNull(agha)) {
            /*
             * Bind vertx
             */
            Ut.contract(agha, Vertx.class, this.vertx);
            /*
             * Invoke here to provide input
             */
            if (DevEnv.devJobBoot()) {
                LOGGER.info(JOB_AGHA_SELECTED, agha.getClass(), mission.getCode(), mission.getType());
            }
            /*
             * If job type is ONCE, it's not started
             */
            if (EmJob.JobType.ONCE != mission.getType()) {
                agha.begin(mission);
            }
        }
        return Ut.future();
    }
}
