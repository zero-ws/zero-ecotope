package io.zerows.cosmic.plugins.job;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Worker;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmService;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;
import lombok.extern.slf4j.Slf4j;

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
@Worker(instances = VValue.SINGLE, thread = ThreadingModel.WORKER)
@Slf4j
public class ZeroScheduler extends AbstractVerticle {

    private static final String JOB_EMPTY = "[ ZERO ] ( Job ) ⚠️ 系统中没有定义任何 Job，当前 Scheduler 将停止。";
    private static final String JOB_CONFIG_NULL = "[ ZERO ] ( Job ) 任务系统未配置，任务容器 Container 将停止....";
    private static final String JOB_MONITOR = "[ ZERO ] ( Job ) ⏳ Zero 检测到 {} 任务 @Job, Scheduler 将启动....";
    private static final String JOB_AGHA_SELECTED = "[ ZERO ] ( Job ) Agha = {} 任务启动器，分派任务 {}, 类型 {}";
    private static final String JOB_STARTED = "[ ZERO ] ( Job ) ✅ 所有任务调度器都成功启动!!!";

    public ZeroScheduler() {
    }

    @Override
    public void start() {
        /* Whether contains JobConfig? */
        final JobConfig config = JobActor.ofConfig();
        if (Objects.nonNull(config)) {
            /* Pick Up all Mission definition from system */
            final JobStore store = JobActor.ofStore();
            final Set<Mission> missions = store.fetch();
            /* Whether there exist Mission definition */
            if (missions.isEmpty()) {
                log.info(JOB_EMPTY);
            } else {
                log.info(JOB_MONITOR, missions.size());
                /* Start each job here by different types */
                final List<Future<Void>> futures = new ArrayList<>();
                missions.forEach(mission -> futures.add(this.start(mission)));
                FnBase.combineT(futures).onSuccess(nil -> log.info(JOB_STARTED));
            }
        } else {
            log.info(JOB_CONFIG_NULL);
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
            log.debug(JOB_AGHA_SELECTED, agha.getClass(), mission.getCode(), mission.getType());
            /*
             * If job type is ONCE, it's not started
             */
            if (EmService.JobType.ONCE != mission.getType()) {
                agha.begin(mission);
            }
        }
        return Ut.future();
    }
}
