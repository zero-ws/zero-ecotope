package io.zerows.cosmic.plugins.job.metadata;

import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 📆 KScheduler — 调度信息封装对象
 *
 * 说明:
 * 1. 封装任务的调度信息（唯一标识、周期、运行公式等）
 * 2. 提供从公式或指定时间点计算下一次触发时间与等待时长的能力
 * 3. duration 与 threshold 等单位在内部做了标准化（duration 以毫秒为基准，threshold 以纳秒表示）
 *
 * 关键点:
 * - waitDuration(): 返回当前周期（毫秒），若未配置则使用默认值（5 分钟）
 * - waitUntil(): 计算下次触发的延迟（毫秒），若为一次性任务则返回 1（避免小于 1ms 的定时器问题）
 * - startTimeMillis(): 返回下次触发时间的时间戳（毫秒）或当前时间
 * </pre>
 */
@Slf4j
public class KScheduler implements Serializable {
    private final String unique;
    /* 使用 java.time.Duration 表示周期，替代原有的 durationUnit + duration 两字段 */
    private Duration duration = null;
    /* 运行公式（可能为复杂的计划表达式） */
    private KPlan formula;

    public KScheduler(final String unique) {
        this.unique = unique;
    }

    // -------------------------- 配置方法 -----------------------------

    /**
     * <pre>
     * 使用公式字符串创建 KPlan（无指定初始运行时间）
     * </pre>
     */
    public KScheduler configure(final String formula) {
        this.formula = new KPlan(formula, null);
        return this;
    }

    /**
     * <pre>
     * 使用公式和指定的 LocalTime（runAt）进行配置
     *
     * 说明:
     * - 当 runAt 为 null 时，仅使用 formula 构建 KPlan
     * - 当提供 runAt 时：如果 runAt 在当天已经过去，则将首次运行时间设为次日的 runAt
     * - 将计算得到的首次触发时间（Instant）作为 KPlan 的基准
     * </pre>
     */
    public KScheduler configure(final String formula, final LocalTime runAt) {
        /* 根据 runAt 做计算 */
        if (Objects.isNull(runAt)) {
            Objects.requireNonNull(formula);
            this.formula = new KPlan(formula, null);
        } else {
            /* 如果提供了 runAt，则需计算具体的首次触发日期时间 */
            final LocalTime runNow = LocalTime.now();
            // 如果 runAt 在今天已过，则需要将日期向后推一天
            LocalDate today = LocalDate.now();
            if (runAt.isBefore(runNow)) {
                // 明天
                today = today.plusDays(1);
            }
            final LocalDateTime dateTime = LocalDateTime.of(today, runAt);
            final Instant instant = Ut.parse(dateTime).toInstant();
            this.formula = new KPlan(formula, instant);
        }
        return this;
    }

    /*
     * 基于 `duration` 与 `unit` 计算并保存最终的周期值
     * - 原先使用 durationUnit + duration 两字段，现使用 java.time.Duration 存储
     * - 内部以毫秒为周期单位保存（用于 setPeriodic）
     *
     * 关于 threshold（阈值）单位说明：threshold 通常以纳秒保存，用于超时判断
     */
    public KScheduler configure(final Duration scheduledAt) {
        Objects.requireNonNull(scheduledAt);
        this.duration = scheduledAt;
        return this;
    }

    // -------------------------- 计算方法 -----------------------------

    public String name() {
        return this.unique;
    }

    /**
     * <pre>
     * 返回等待的周期时长（毫秒）
     *
     * 说明:
     * - 如果未配置周期（duration == null），返回默认值 5 分钟（毫秒）
     * - 否则返回已配置的周期（毫秒）
     * </pre>
     */
    public long waitDuration() {
        // 默认 5 分钟
        if (Objects.isNull(this.duration)) {
            return TimeUnit.MINUTES.toMillis(5);
        } else {
            return this.duration.toMillis();
        }
    }

    /**
     * <pre>
     * 计算下一次触发的延迟（毫秒）
     *
     * 说明:
     * - 如果公式中的 runAt 为 null，说明为一次性或不可计算的情况，返回 1（避免小于 1ms 的定时器异常）
     * - 否则计算当前时间到 runAt 的差值（毫秒），若为负数则返回 1
     * - 若 delay > 0，则会打印日志，包含 human-readable 时间（使用公式的 formatter）
     * </pre>
     */
    public long waitUntil() {
        final Instant end = this.formula.runAt();
        if (Objects.isNull(end)) {
            /*
             * 处理 delay < 1ms 的问题，返回值为 1 ms，避免无法创建定时器
             * 该场景通常对应一次性任务（ONCE）
             */
            return 1;
        } else {
            /*
             * 非一次性任务：计算从现在到下次运行的毫秒差
             */
            final Instant start = Instant.now();
            final long delay = ChronoUnit.MILLIS.between(start, end);
            if (0 < delay) {
                final DateTimeFormatter formatter = this.formula.formatter();
                if (Objects.nonNull(formatter)) {
                    final LocalDateTime datetime = Ut.toDuration(delay);
                    log.info("[ ZERO ] 任务 \"{}\" 将在 `{}` 之后运行……",
                        this.unique, formatter.format(datetime));
                }
            }
            return delay < 0 ? 1L : delay;
        }
    }

    @Override
    public String toString() {
        return "KScheduler{" +
            "unique='" + this.unique + '\'' +
            ", duration=" + (this.duration == null ? "<unset>" : this.duration.toString()) +
            ", formula=" + this.formula +
            '}';
    }

    /**
     * 返回下次触发时间的 epoch 毫秒，如果无法计算则返回当前时间
     */
    public long startTimeMillis() {
        final Instant end = this.formula.runAt();
        return end != null ? end.toEpochMilli() : System.currentTimeMillis();
    }
}
