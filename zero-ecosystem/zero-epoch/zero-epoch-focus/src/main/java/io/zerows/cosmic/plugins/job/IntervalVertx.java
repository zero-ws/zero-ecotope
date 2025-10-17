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

@Slf4j
public class IntervalVertx implements Interval {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm.ss.SSS");
    /*
     * Fix issue of delay < 1ms, the default should be 1
     * Cannot schedule a timer with delay < 1 ms
     */
    private static final int START_UP_MS = 1;

    @Contract
    private transient Vertx vertx;

    private Consumer<Long> controlFn;

    @Override
    public Interval bind(final Consumer<Long> controlFn) {
        this.controlFn = controlFn;
        return this;
    }

    /*
     * This situation is for executing without any `delay` part
     * here, although you have set the `delay` parameter in `Timer` here
     * but the code logical will ignore `delay`.
     *
     * For `delay` part the system should avoid following issue:
     * Fix issue of delay < 1ms, the default should be 1, Cannot schedule a timer with delay < 1 ms
     *
     * The 1ms is started. When following condition has been triggered, here are two code logical
     *
     * 1) KScheduler is null        ( Once Job )
     * 2) KScheduler is not null    ( Legacy Plan Job )
     */
    @Override
    public void startAt(final Handler<Long> actuator, final KScheduler timer) {
        if (Objects.isNull(timer)) {
            /*
             * Because timer is null, delay ms is not needed
             * In this kind of situation
             * call vertx.setTimer only, the smallest is 1ms ( Right Now )
             */
            log.info("[ ZERO ] ( Job ) Timer = null 未设置，任务将立即启动！");
            this.vertx.setTimer(START_UP_MS, actuator);
        } else {
            /*
             * Extract delay ms from `timer` reference
             * Be careful about the timerId here, the returned timerId
             *
             * Here are two timerId
             * 1. setTimer          ( Returned Directly )
             * 2. setPeriodic       ( Output by actuator )
             */
            final long now = System.currentTimeMillis();
            final long startTime = timer.startTimeMillis();  // 获取计划任务的绝对时间戳
            final long delay = Math.max(startTime - now, 0L); // 如果已经过了时间点，delay = 0
            final long duration = timer.waitDuration();      // 间隔周期（毫秒）

            if (delay <= 0) {
                // 当前时间 >= 设定时间，立即执行一次
                log.info("[ ZERO ] ( Job ) 当前时间 >= 设定时间，任务将立即启动！");
                actuator.handle(null); // 立即执行

                // 设置周期任务
                final long timerId = this.vertx.setPeriodic(duration, actuator);
                /*
                 * Bind the controlFn to consume the timerId of periodic timer
                 * In the document of vert.x here are comments:
                 *
                 * To cancel a periodic timer, call cancelTimer specifying the timer id. For example:
                 * vertx.cancelTimer(timerID);
                 */
                log.info("[ ZERO ] ( Job ) 计时器 {}, `{}` 将每隔 {} 调度运行一次！",
                    timerId, timer.name(), duration);
                if (Objects.nonNull(this.controlFn)) {
                    this.controlFn.accept(timerId);
                }
            } else {
                // 当前时间 < 设定时间，延迟 delay 毫秒后开始第一次任务
                log.info("[ ZERO ] ( Job ) 调度器 {} 将在 {} 后启动！", timer.name(), FORMATTER.format(Ut.toDuration(delay)));

                this.vertx.setTimer(delay + START_UP_MS, ignored -> {
                    log.info("[ ZERO ] ( Job ) 触发器在预定时间首次执行任务！");
                    actuator.handle(null); // 第一次执行

                    // 设置周期任务
                    final long timerId = this.vertx.setPeriodic(duration, actuator);
                    log.info("[ ZERO ] ( Job ) 计时器 {} 首次触发, `{}` 将每隔 {} 调度运行一次！",
                        timerId, timer.name(), duration);
                    if (Objects.nonNull(this.controlFn)) {
                        this.controlFn.accept(timerId);
                    }
                });
            }
        }
    }


    @Override
    public void restartAt(final Handler<Long> actuator, final KScheduler timer) {
        if (Objects.isNull(timer)) {
            log.info("[ ZERO ] ( Job ) Timer = null 未设置，任务将立即重启！");
            this.vertx.setTimer(START_UP_MS, actuator);
        } else {
            final long waitSec = timer.waitUntil();
            final long delay = waitSec + START_UP_MS;
            this.vertx.setTimer(delay, actuator);
            log.info("[ ZERO ] ( Job ) 计时器 {}, `{}` 将在 {} 后重启！",
                timer.name(), timer.name(), FORMATTER.format(Ut.toDuration(waitSec)));
        }
    }
}
