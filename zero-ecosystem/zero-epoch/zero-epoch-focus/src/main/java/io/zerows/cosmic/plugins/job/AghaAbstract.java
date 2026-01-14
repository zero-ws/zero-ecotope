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

/**
 * <pre>
 * 🔗 AghaAbstract — 任务执行链逻辑抽象基类。
 *
 * 说明:
 * 定义了标准的任务执行流程（Pipeline），涵盖从输入到输出及回调的全过程。
 *
 * 🔄 执行流程 (Workflow):
 * 1. 📥 输入源 (Input Source):
 *    - 数据源于 `incomeAddress`（可能包含预处理或其他信息）。
 *
 * 2. 🧩 Income 组件 (Pre-Processor):
 *    - 如果配置了 `incomeComponent`，则触发该组件执行前置逻辑。
 *
 * 3. ⚙️ 核心组件 (Core Processor):
 *    - `component` 是必须的，包含任务的核心业务逻辑。
 *
 * 4. 🧩 Outcome 组件 (Post-Processor):
 *    - 如果配置了 `outcomeComponent`，则触发该组件执行后置逻辑。
 *
 * 5. 📤 输出源 (Output Source):
 *    - 结果消息将被发送到 `outcomeAddress`。
 *
 * 6. 🔙 回调 (Callback):
 *    - 执行完毕后可能触发 `callbackAsync` 进行回调处理：
 *      - 如果存在 `outcomeAddress`，数据来自 Event Bus。
 *      - 否则，数据直接来自 `outcomeComponent` 的输出。
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AghaAbstract implements Agha {

    private static final AtomicBoolean SELECTED = new AtomicBoolean(Boolean.TRUE);
    /**
     * <pre>
     * 🚦 任务状态机流转图 (Job Status Machine)
     *
     * STARTING ------|
     *                v
     *     |------> READY <-------------------|
     *     |          |                       |
     *     |          |                    &lt;start&gt;
     *     |          |                       |
     *     |        &lt;start&gt;                   |
     *     |          |                       |
     *     |          V                       |
     *     |        RUNNING --- &lt;stop&gt; ---&gt; STOPPED
     *     |          |
     *     |          |
     *  &lt;resume&gt;   ( error )
     *     |          |
     *     |          |
     *     |          v
     *     |------- ERROR
     * </pre>
     */
    private static final ConcurrentMap<EmService.JobStatus, EmService.JobStatus> VM = new ConcurrentHashMap<>() {
        {
            /* 初始化状态流转：STARTING -> READY */
            this.put(EmService.JobStatus.STARTING, EmService.JobStatus.READY);

            /* 自动流转：READY -> RUNNING */
            this.put(EmService.JobStatus.READY, EmService.JobStatus.RUNNING);

            /* 自动流转：RUNNING -> STOPPED */
            this.put(EmService.JobStatus.RUNNING, EmService.JobStatus.STOPPED);

            /* 手动/触发流转：STOPPED -> READY */
            this.put(EmService.JobStatus.STOPPED, EmService.JobStatus.READY);

            /* 错误恢复：ERROR -> READY */
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

    /**
     * <pre>
     * ⚡️ workingAsync - 异步任务执行链
     *
     * 说明:
     * 构建并执行 Mission 的完整工作流。
     *
     * 📋 步骤说明:
     * 1. 📬 地址检查 (Input Check):
     *    - 是：从 Event Bus 获取 Envelop 作为辅助输入。
     *    - 否：使用 `Envelop.ok()` 作为默认输入。
     *
     * 2. 📥 Income 提取 (Pre-Process):
     *    - 执行 JobIncome 逻辑。
     *
     * 3. ⚙️ 核心逻辑 (Core Execution):
     *    - 执行主要业务代码 (Component)。
     *
     * 4. 📤 Outcome 处理 (Post-Process):
     *    - 执行 JobOutcome 逻辑。
     *
     * 5. 📡 输出检查 (Output Check):
     *    - 检查是否定义了输出地址，如果有则发送结果。
     *
     * 6. 🔙 回调 (Callback):
     *    - 提供任务完成后的回调钩子。
     * </pre>
     *
     * @param mission 任务元数据对象
     * @return Future&lt;Envelop&gt; 异步执行结果
     */
    private Future<Envelop> workingAsync(final Mission mission) {
        /*
         * 初始化 Phase 引用，用于构建执行链
         */
        final Phase phase = Phase.start(mission.getCode())
            .bind(this.vertx)
            .bind(mission);
        /*
         * 1. 步骤 1: EventBus ( 输入源 )
         */
        return phase.inputAsync(mission)
            /*
             * 2. 步骤 2: JobIncome ( 前置处理 )
             */
            .compose(phase::incomeAsync)
            /*
             * 3. 步骤 3: 核心业务逻辑代码
             */
            .compose(phase::invokeAsync)
            /*
             * 4. 步骤 4: JobOutcome ( 后置处理 )
             */
            .compose(phase::outcomeAsync)
            /*
             * 5. 步骤 5: EventBus ( 输出源 )
             */
            .compose(phase::outputAsync)
            /*
             * 6. 最终步骤：回调处理
             */
            .compose(phase::callbackAsync);
    }

    void working(final Mission mission, final Actuator actuator) {
        if (EmService.JobStatus.READY == mission.getStatus()) {
            /*
             * 状态变更：READY -> RUNNING
             */
            this.moveOn(mission, true);
            /*
             * 读取超时阈值
             * 「注意」旧版本代码中 KScheduler 若为 null 可能导致问题，
             * 但在 ONCE 或特定类型中，timer 可能确实为 null。
             * 此处直接从 mission 获取计算好的 timeout。
             */
            final long threshold = mission.timeout();
            /*
             * 创建新的 Worker Executor
             * 1) 为下一次执行创建独立的 worker 线程池
             * 2) 不要阻塞主线程，避免影响当前任务的终止操作
             * 3) 在此处执行，解决长时间阻塞的问题（设置超时时间）
             */
            final String code = mission.getCode();
            final WorkerExecutor executor =
                this.vertx.createSharedWorkerExecutor(code, 1, threshold);
            this.log().debug("[ ZERO ] ( Job ) 任务执行器 {} 已创建，最大执行时间 {} 秒",
                code, TimeUnit.NANOSECONDS.toSeconds(threshold));
            executor.executeBlocking(() -> this.workingAsync(mission)
                .compose(result -> {
                    /*
                     * 任务执行成功，触发 Actuator 后置逻辑
                     */
                    Fn.jvmAt(actuator);
                    this.log().info("[ ZERO ] ( Job ) 任务执行器 {} 执行完成，准备关闭！", code);
                    return Future.succeededFuture(result);
                })
                .otherwise(error -> {
                    /*
                     * 任务执行异常处理
                     */
                    if (!(error instanceof VertxException)) {
                        this.log().error(error.getMessage(), error);
                        // 标记任务状态为异常，但不中断流程
                        this.moveOn(mission, false);
                    }
                    return Envelop.failure(error);
                })
            ).onComplete(handler -> {
                /*
                 * 异步结果检查是否完成
                 */
                if (handler.succeeded()) {
                    /*
                     * 成功，关闭 worker executor 释放资源
                     */
                    executor.close();
                } else {
                    if (Objects.nonNull(handler.cause())) {
                        /*
                         * 失败，打印堆栈而不是吞掉异常
                         * 忽略 VertxException (如 Thread blocked)，避免日志噪音
                         */
                        final Throwable error = handler.cause();
                        if (!(error instanceof VertxException)) {
                            this.log().error(error.getMessage(), error);
                        }
                    }
                }
            }).otherwise(error -> {
                // 最后的防线，记录未捕获的异常
                this.log().error(error.getMessage(), error);
                return null;
            });
        }
    }

    void moveOn(final Mission mission, final boolean noError) {
        if (noError) {
            /*
             * 任务准备阶段
             **/
            if (VM.containsKey(mission.getStatus())) {
                /*
                 * 计算下一个状态
                 */
                final EmService.JobStatus moved = VM.get(mission.getStatus());
                final EmService.JobStatus original = mission.getStatus();
                mission.setStatus(moved);
                /*
                 * 记录日志并更新存储中的状态缓存
                 */
                this.log().info("[ ZERO ] ( Job ) \uD83D\uDCAB 状态：{} -> {}，(类型：{} / 编码：{})",
                    original, moved, mission.getType(), mission.getCode());
                this.store().update(mission);
            }
        } else {
            /*
             * 任务终止阶段（异常情况）
             */
            if (EmService.JobStatus.RUNNING == mission.getStatus()) {
                mission.setStatus(EmService.JobStatus.ERROR);
                this.log().error("[ ZERO ] ( Job ) \uD83D\uDCAB 状态：RUNNING -> ERROR，(类型：{} / 编码：{})",
                    mission.getType(), mission.getCode());
                this.store().update(mission);
            }
        }
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected Vertx vertx() {
        return this.vertx;
    }
}
