package io.zerows.cosmic.plugins.job;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.WorkerExecutor;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Contract;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.enums.EmService;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/*
 * The chain should be
 *
 * 1) Input data came from 'incomeAddress' ( There are some preparing or other info )
 * 2) `incomeComponent` will be triggered if it's existing.
 * 3) `component` is required and contain major code logical.
 * 4) `outcomeComponent` will be triggered if it's existing.
 * 5) The result message will be sent to `outcomeAddress`.
 * 6) There could be a callbackAsync method for callback execution ( After Out )
 *    - If `outcomeAddress`, the data came from Event Bus
 *    - Otherwise, the data came from `outcomeComponent`.
 */
@SuppressWarnings("all")
public abstract class AghaAbstract implements Agha {

    private static final AtomicBoolean SELECTED = new AtomicBoolean(Boolean.TRUE);
    /*
     * STARTING ------|
     *                v
     *     |------> READY <-------------------|
     *     |          |                       |
     *     |          |                    <start>
     *     |          |                       |
     *     |        <start>                   |
     *     |          |                       |
     *     |          V                       |
     *     |        RUNNING --- <stop> ---> STOPPED
     *     |          |
     *     |          |
     *  <resume>   ( error )
     *     |          |
     *     |          |
     *     |          v
     *     |------- ERROR
     *
     */
    private static final ConcurrentMap<EmService.JobStatus, EmService.JobStatus> VM = new ConcurrentHashMap<>() {
        {
            /* STARTING -> READY */
            this.put(EmService.JobStatus.STARTING, EmService.JobStatus.READY);

            /* READY -> RUNNING ( Automatically ) */
            this.put(EmService.JobStatus.READY, EmService.JobStatus.RUNNING);

            /* RUNNING -> STOPPED ( Automatically ) */
            this.put(EmService.JobStatus.RUNNING, EmService.JobStatus.STOPPED);

            /* STOPPED -> READY */
            this.put(EmService.JobStatus.STOPPED, EmService.JobStatus.READY);

            /* ERROR -> READY */
            this.put(EmService.JobStatus.ERROR, EmService.JobStatus.READY);
        }
    };
    @Contract
    private transient Vertx vertx;

    JobInterval interval(final Consumer<Long> consumer) {
        final JobInterval interval = JobActor.ofInterval();
        if (Objects.isNull(interval)) {
            this.log().error("[ ZERO ] ( Job ) 任务调度组件未正确配置，无法执行任务调度，请检查配置！");
            return null;
        }
        Ut.contract(interval, Vertx.class, this.vertx);

        if (SELECTED.getAndSet(Boolean.FALSE)) {
            /* Be sure the info only provide once */
            this.log().info("[ ZERO ] ( Job ) 任务选择了定时组件 {}", interval.getClass().getName());
        }
        if (Objects.nonNull(consumer)) {
            interval.bind(consumer);
        }
        return interval;
    }

    JobInterval interval() {
        return this.interval(null);
    }

    JobStore store() {
        return JobActor.ofStore();
    }

    /*
     * Input workflow for Mission
     * 1. Whether address configured ?
     *    - Yes, findRunning Envelop from event bus as secondary input
     *    - No, findRunning Envelop of `Envelop.ok()` instead
     * 2. Extract `JobIncome`
     * 3. Major
     * 4. JobOutcome
     * 5. Whether defined address of output
     * 6. If 5, provide callback function of this job here.
     */
    private Future<Envelop> workingAsync(final Mission mission) {
        /*
         * Initializing phase reference here.
         */
        final Phase phase = Phase.start(mission.getCode())
            .bind(this.vertx)
            .bind(mission);
        /*
         * 1. Step 1:  EventBus ( Input )
         */
        return phase.inputAsync(mission)
            /*
             * 2. Step 2:  JobIncome ( Process )
             */
            .compose(phase::incomeAsync)
            /*
             * 3. Step 3:  Major cole logical here
             */
            .compose(phase::invokeAsync)
            /*
             * 4. Step 4:  JobOutcome ( Process )
             */
            .compose(phase::outcomeAsync)
            /*
             * 5. Step 5: EventBus ( Output )
             */
            .compose(phase::outputAsync)
            /*
             * 6. Final steps here
             */
            .compose(phase::callbackAsync);
    }

    void working(final Mission mission, final Actuator actuator) {
        if (EmService.JobStatus.READY == mission.getStatus()) {
            /*
             * READY -> RUNNING
             */
            this.moveOn(mission, true);
            /*
             * Read threshold
             * 「OLD」for KScheduler not null, but in ONCE or some spec types,
             * the timer could be null
             * final KScheduler timer = mission.timer();
             * Objects.requireNonNull(timer);
             */
            final long threshold = mission.timeout();
            /*
             * Worker Executor of New created
             * 1) Create new worker pool for next execution here
             * 2) Do not break the major thread for terminal current job
             * 3）Executing info here for long block issue
             */
            final String code = mission.getCode();
            final WorkerExecutor executor =
                this.vertx.createSharedWorkerExecutor(code, 1, threshold);
            this.log().info("[ ZERO ] ( Job ) 任务执行器 {} 已创建，最大执行时间 {} 秒",
                code, TimeUnit.NANOSECONDS.toSeconds(threshold));
            executor.<Envelop>executeBlocking(() -> {
                // 在 executeBlocking 的 Callable 中，直接执行阻塞逻辑
                return this.workingAsync(mission)
                    .compose(result -> {
                        /*
                         * 任务执行成功，执行后置逻辑
                         */
                        Fn.jvmAt(actuator);
                        this.log().info("[ ZERO ] ( Job ) 任务执行器 {} 执行完成，准备关闭！", code);
                        return Future.succeededFuture(result);
                    })
                    .otherwise(error -> {
                        /*
                         * 任务执行异常
                         */
                        if (!(error instanceof VertxException)) {
                            error.printStackTrace();
                            this.moveOn(mission, false);
                        }
                        return Envelop.failure(error);
                    })
                    // 等待异步结果（因为 Callable 要返回 T，这里要阻塞获取）
                    .toCompletionStage().toCompletableFuture().get();
            }).onComplete(handler -> {
                /*
                 * 异步结果检查是否完成
                 */
                if (handler.succeeded()) {
                    /*
                     * 成功，关闭 worker executor
                     */
                    executor.close();
                } else {
                    if (Objects.nonNull(handler.cause())) {
                        /*
                         * 失败，打印堆栈而不是吞掉异常
                         */
                        final Throwable error = handler.cause();
                        if (!(error instanceof VertxException)) {
                            error.printStackTrace();
                        }
                    }
                }
            }).otherwise(error -> {
                error.printStackTrace();
                return null;
            });
        }
    }

    void moveOn(final Mission mission, final boolean noError) {
        if (noError) {
            /*
             * Preparing for job
             **/
            if (VM.containsKey(mission.getStatus())) {
                /*
                 * Next Status
                 */
                final EmService.JobStatus moved = VM.get(mission.getStatus());
                final EmService.JobStatus original = mission.getStatus();
                mission.setStatus(moved);
                /*
                 * Log and update cache
                 */
                this.log().info("[ ZERO ] ( Job ) 任务状态变更：{} -> {}，任务类型：{}，任务编码：{}",
                    original, moved, mission.getType(), mission.getCode());
                this.store().update(mission);
            }
        } else {
            /*
             * Terminal job here
             */
            if (EmService.JobStatus.RUNNING == mission.getStatus()) {
                mission.setStatus(EmService.JobStatus.ERROR);
                this.log().error("[ ZERO ] ( Job ) 任务状态变更：RUNNING -> ERROR，任务类型：{}，任务编码：{}",
                    mission.getType(), mission.getCode());
                this.store().update(mission);
            }
        }
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
