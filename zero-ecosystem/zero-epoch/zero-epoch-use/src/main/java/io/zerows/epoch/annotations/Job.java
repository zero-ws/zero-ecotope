package io.zerows.epoch.annotations;

import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.EmService;

import java.lang.annotation.*;

/**
 * <pre>
 * ✅ Job 注解 — 标记由 Zero 扫描并由 ZeroScheduler 启动的任务
 *
 * 说明:
 * 1. 用于标注框架管理的任务类型，Zero 会扫描带有此注解的类并根据注解信息注册到调度器
 * 2. 任务分类:
 *    1) FIXED：由框架生成并通过定时器管理，在固定时间点触发
 *    2) SCHEDULED：由框架生成并通过调度器管理，从启动时间开始按单位周期循环触发
 *    3) ONCE：由其他任务或手动触发的单次任务，但仍会存储到 JobStore 中
 * 3. 注解语义提示：在注解里，通常只在 type = SCHEDULED 场景下使用复杂计划（formula）
 *
 * Emoji 高亮:
 * ⚙️ 表示配置项
 * ⏱️ 表示时间/周期相关
 * ℹ️ 表示说明或约束
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Job {
    /**
     * <pre>
     * ⚠️ 必填：任务类型
     *
     * 说明:
     * - 指定任务的类型，使用枚举 EmService.JobType
     * - 该值决定任务的调度与生命周期管理策略
     * </pre>
     */
    EmService.JobType value();

    /**
     * <pre>
     * 🏷️ 默认名称（可选）
     *
     * 说明:
     * - 如果不指定 name，框架会自动生成默认任务名
     * - 提供自定义名称可以提高可读性与管理便利性
     * </pre>
     */
    String name() default VString.EMPTY;

    /**
     * <pre>
     * ⚙️ 配置存储路径（可选）
     *
     * 说明:
     * - 当任务定义较为复杂时，可通过配置文件或存储路径来扩展任务定义
     * - 该字段表示配置数据在存储中的路径或标识
     * </pre>
     */
    String config() default VString.EMPTY;

    /**
     * <pre>
     * ⏱️ value 持续时间（默认 30s）
     *
     * 说明:
     * - 用于控制任务的 "value" 持续窗口，默认值为 "30s"
     * - 格式以秒为单位，支持带时间单位的字符串表示（例如 "30s"）
     * </pre>
     */
    String duration() default "30s";

    /**
     * <pre>
     * ⏱️ value 阈值（默认 900s）
     *
     * 说明:
     * - 用于控制任务被认为超时或失效的阈值，默认值为 "900s"
     * - 格式同样为带单位的字符串表示（例如 "900s"）
     * </pre>
     */
    String threshold() default "900s";

    /**
     * <pre>
     * 🧭 智能时间格式（formula，可选）
     *
     * 说明:
     * - 用于描述基于模式的触发时间（例如按天/周/月/季/年触发的复杂表达式）
     * - 支持的模式示例:
     *   1) D,00:00,... 每日在指定时刻触发
     *   2) W,00:00/3,... 每周在第 N 天或间隔触发
     *   3) M,00:00/4,... 每月在指定日或间隔触发
     *   4) Q,00:00/33,... 每季度按设定触发
     *   5) Y,00:00/2-22,.. 每年在指定日期触发
     * - 仅在需要复杂调度时填写，常规场景可置空
     * </pre>
     */
    String formula() default VString.EMPTY;
}
