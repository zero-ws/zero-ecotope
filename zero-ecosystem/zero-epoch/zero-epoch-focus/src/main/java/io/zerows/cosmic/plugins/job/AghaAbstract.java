package io.zerows.cosmic.plugins.job;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.WorkerExecutor;
import io.zerows.component.log.OLog;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Contract;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.enums.EmJob;
import io.zerows.support.Ut;

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
public abstract class AghaAbstract implements Agha {

    private static final JobConfig CONFIG = JobPin.getConfig();
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
    private static final ConcurrentMap<EmJob.Status, EmJob.Status> VM = new ConcurrentHashMap<>() {
        {
            /* STARTING -> READY */
            this.put(EmJob.Status.STARTING, EmJob.Status.READY);

            /* READY -> RUNNING ( Automatically ) */
            this.put(EmJob.Status.READY, EmJob.Status.RUNNING);

            /* RUNNING -> STOPPED ( Automatically ) */
            this.put(EmJob.Status.RUNNING, EmJob.Status.STOPPED);

            /* STOPPED -> READY */
            this.put(EmJob.Status.STOPPED, EmJob.Status.READY);

            /* ERROR -> READY */
            this.put(EmJob.Status.ERROR, EmJob.Status.READY);
        }
    };
    @Contract
    private transient Vertx vertx;

    Interval interval(final Consumer<Long> consumer) {
        final Class<?> intervalCls = CONFIG.getInterval().getComponent();
        final Interval interval = Ut.singleton(intervalCls);
        Ut.contract(interval, Vertx.class, this.vertx);

        if (SELECTED.getAndSet(Boolean.FALSE)) {
            /* Be sure the log only provide once */
            this.logger().info(JobMessage.PHASE.UCA_COMPONENT, "Interval", interval.getClass().getName());
        }
        if (Objects.nonNull(consumer)) {
            interval.bind(consumer);
        }
        return interval;
    }

    Interval interval() {
        return this.interval(null);
    }

    JobStore store() {
        return JobPin.getStore();
    }

    /*
     * Input workflow for Mission
     * 1. Whether address configured ?
     *    - Yes, get Envelop from event bus as secondary input
     *    - No, get Envelop of `Envelop.ok()` instead
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
        if (EmJob.Status.READY == mission.getStatus()) {
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
             * 3）Executing log here for long block issue
             */
            final String code = mission.getCode();
            final WorkerExecutor executor =
                this.vertx.createSharedWorkerExecutor(code, 1, threshold);
            this.logger().info(JobMessage.AGHA.WORKER_START, code, String.valueOf(TimeUnit.NANOSECONDS.toSeconds(threshold)));
            /*
             * The executor start to process the workers here.
             */
            //            executor.<Envelop>executeBlocking(promise -> promise.handle(this.workingAsync(mission)
            //                .compose(result -> {
            //                    /*
            //                     * The job is executing successfully and then stopped
            //                     */
            //                    actuator.execute();
            //                    this.logger().info(MessageOfJob.AGHA.WORKER_END, code);
            //                    return Future.succeededFuture(result);
            //                })
            //                .otherwise(error -> {
            //                    /*
            //                     * The job exception
            //                     */
            //                    if (!(error instanceof NoStackTraceThrowable)) {
            //                        error.printStackTrace();
            //                        this.moveOn(mission, false);
            //                    }
            //                    return Envelop.failure(error);
            //                })), handler -> {
            //                /*
            //                 * Async result here to check whether it's ended
            //                 */
            //                if (handler.succeeded()) {
            //                    /*
            //                     * Successful, close worker executor
            //                     */
            //                    executor.close();
            //                } else {
            //                    if (Objects.nonNull(handler.cause())) {
            //                        /*
            //                         * Failure, print stack instead of other exception here.
            //                         */
            //                        final Throwable error = handler.cause();
            //                        if (!(error instanceof NoStackTraceThrowable)) {
            //                            error.printStackTrace();
            //                        }
            //                    }
            //                }
            //            });
            executor.<Envelop>executeBlocking(() -> {
                // 在 executeBlocking 的 Callable 中，直接执行阻塞逻辑
                return this.workingAsync(mission)
                    .compose(result -> {
                        /*
                         * 任务执行成功，执行后置逻辑
                         */
                        Fn.jvmAt(actuator);
                        this.logger().info(JobMessage.AGHA.WORKER_END, code);
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
                final EmJob.Status moved = VM.get(mission.getStatus());
                final EmJob.Status original = mission.getStatus();
                mission.setStatus(moved);
                /*
                 * Log and update cache
                 */
                this.logger().info(JobMessage.AGHA.MOVED, mission.getType(), mission.getCode(), original, moved);
                this.store().update(mission);
            }
        } else {
            /*
             * Terminal job here
             */
            if (EmJob.Status.RUNNING == mission.getStatus()) {
                mission.setStatus(EmJob.Status.ERROR);
                this.logger().info(JobMessage.AGHA.TERMINAL, mission.getType(), mission.getCode());
                this.store().update(mission);
            }
        }
    }

    protected OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
