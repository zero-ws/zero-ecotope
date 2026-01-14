package io.zerows.cosmic.plugins.job;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.zerows.cosmic.plugins.job.metadata.KScheduler;
import io.zerows.epoch.annotations.Contract;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <pre>
 * ⚡ JobIntervalVertx — 基于 Vert.x 的任务调度实现
 *
 * 说明:
 * 1. 使用 Vert.x 的定时器 (setTimer / setPeriodic) 实现 JobInterval 行为
 * 2. 处理两类场景：一次性任务（timer 为 null）与周期任务（基于 KScheduler 的计划）
 * 3. 解决最小延迟问题（Vert.x 不允许 delay < 1ms），因此使用常量 START_UP_MS 保证最小延迟
 *
 * 关键点与职责:
 * - 当 timer 为 null 时，立即触发一次执行（适用于 ONCE/即时执行场景）
 * - 当 timer 存在且延迟 <= 0 时：先触发一次执行，再用 setPeriodic 建立周期任务
 * - 当 timer 存在且延迟 > 0 时：延迟后触发首次执行，随后建立周期任务
 * - 在创建周期任务后，会将返回的 timerId 通过 controlFn 回传（如有绑定）以便外部取消
 *
 * 注意事项:
 * 🔧 START_UP_MS = 1 表示程序会在 1 毫秒内尝试触发，避免 Vert.x 对小于 1ms 的延迟抛错
 * 🔁 首次执行（立即或延迟）会明确调用 actuator.handle(null) 以保证与周期任务的一致性
 * ⚠️ log 信息包含任务名、timerId 与周期长度，便于排查与监控
 * </pre>
 */
@Slf4j
public class JobIntervalVertx implements JobInterval {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm.ss.SSS");
    /*
     * 修复 delay < 1ms 的问题，Vert.x 不允许小于 1ms 的延迟
     * 最小延迟设置为 1 毫秒
     */
    private static final int START_UP_MS = 1;
    @Contract
    private transient Vertx vertx;
    private Consumer<Long> controlFn;

    /**
     * <pre>
     * 🔗 绑定控制回调
     *
     * 说明:
     * - 将一个 Consumer<Long> 绑定到本实现，用于接收周期任务的 timerId
     * - 绑定后，当创建周期任务（setPeriodic）时会通过 controlFn.accept(timerId) 通知外部
     *
     * 参数:
     * controlFn: 接收 timerId（Long）以便外部可以调用 vertx.cancelTimer(timerId)
     * </pre>
     */
    @Override
    public JobInterval bind(final Consumer<Long> controlFn) {
        this.controlFn = controlFn;
        return this;
    }

    /**
     * <pre>
     * ▶️ 启动或调度任务
     *
     * 说明:
     * - 根据传入的 KScheduler 决定是即时执行（timer == null）还是按计划执行
     * - 场景分支:
     *   1) timer == null: 立即通过 vertx.setTimer(START_UP_MS, actuator) 触发一次执行
     *   2) timer != null:
     *      - 计算 delay = max(startTime - now, 0)
     *      - 若 delay <= 0: 立即执行一次（actuator.handle(null)），随后通过 setPeriodic 建立周期任务
     *      - 若 delay > 0: 使用 setTimer(delay + START_UP_MS, ...) 在延迟后首次触发，首次触发后建立周期任务
     *
     * 约定与实现细节:
     * - 对于周期任务，duration = timer.waitDuration() 表示周期（毫秒）
     * - 在建立周期任务后，会将得到的 timerId 通过 controlFn 回调返回（若 controlFn 非空）
     * - 日志包含触发与周期信息，便于排查
     * </pre>
     */
    @Override
    public void startAt(final Handler<Long> actuator, final KScheduler timer) {
        if (Objects.isNull(timer)) {
            /*
             * timer 为空，表示无需 delay，直接使用最小延迟触发一次
             */
            this.vertx.setTimer(START_UP_MS, actuator);
        } else {
            /*
             * 从 timer 中提取信息并处理延迟/周期
             */
            final long now = System.currentTimeMillis();
            final long startTime = timer.startTimeMillis();  // 获取计划任务的绝对时间戳
            final long delay = Math.max(startTime - now, 0L); // 如果已经过了时间点，delay = 0
            final long duration = timer.waitDuration();      // 间隔周期（毫秒）

            if (delay <= 0) {
                // 当前时间 >= 设定时间，立即执行一次
                actuator.handle(null); // 立即执行

                // 设置周期任务
                final long timerId = this.vertx.setPeriodic(duration, actuator);
                /*
                 * 将周期任务的 timerId 通过 controlFn 通知外部，以便外部取消
                 */
                log.info("[ ZERO ] ( Job ) 周期任务启动: id={}, name={}, duration={}ms",
                    timerId, timer.name(), duration);
                if (Objects.nonNull(this.controlFn)) {
                    this.controlFn.accept(timerId);
                }
            } else {
                // 当前时间 < 设定时间，延迟 delay 毫秒后开始第一次任务
                log.info("[ ZERO ] ( Job ) 延迟启动: name={}, delay={}", timer.name(), FORMATTER.format(Ut.toDuration(delay)));

                this.vertx.setTimer(delay + START_UP_MS, ignored -> {
                    actuator.handle(null); // 第一次执行

                    // 设置周期任务
                    final long timerId = this.vertx.setPeriodic(duration, actuator);
                    log.info("[ ZERO ] ( Job ) 周期任务延续: id={}, name={}, duration={}ms",
                        timerId, timer.name(), duration);
                    if (Objects.nonNull(this.controlFn)) {
                        this.controlFn.accept(timerId);
                    }
                });
            }
        }
    }


    /**
     * <pre>
     * 🔄 重启任务（用于恢复或重新调度）
     *
     * 说明:
     * - 当 timer 为 null 时，行为等同于 startAt（立即触发一次）
     * - 当 timer 存在时，计算 waitSec 并在 waitSec + START_UP_MS 后通过 setTimer 触发
     * - 日志会记录重启延迟信息，方便监控
     * </pre>
     */
    @Override
    public void restartAt(final Handler<Long> actuator, final KScheduler timer) {
        if (Objects.isNull(timer)) {
            this.vertx.setTimer(START_UP_MS, actuator);
        } else {
            final long waitSec = timer.waitUntil();
            final long delay = waitSec + START_UP_MS;
            this.vertx.setTimer(delay, actuator);
            log.debug("[ ZERO ] ( Job ) 任务重启: name={}, delay={}",
                timer.name(), FORMATTER.format(Ut.toDuration(waitSec)));
        }
    }
}
