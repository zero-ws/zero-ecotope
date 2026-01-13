package io.zerows.cosmic.plugins.job;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.KScheduler;

import java.util.function.Consumer;

/**
 * <pre>
 * 📅 JobInterval — 任务调度周期接口
 *
 * 说明:
 * 1. 定义扩展任务的调度策略（替代简单的 FIXED / PLAN / ONCE）
 * 2. 支持更丰富的周期类型：MONTH、WEEK、QUARTER、YEAR 等
 * 3. 将调度过程拆解为规范化阶段：等待（Wait）、执行（Run）、重复或结束（Repeat/End）、以及更新下一次运行时间（Update runAt）
 *
 * 特性与职责:
 * - 提供绑定控制回调（bind）以接收控制指令
 * - 提供 configure 接口用于注入配置
 * - 提供 startAt / restartAt 两种启动方式，支持传入 KScheduler 作为调度上下文
 *
 * 关注点:
 * 🔁 支持迭代与重复的长时任务
 * ⚠️ ONCE 类型应被特殊处理（只运行一次）
 * 🔧 configure 用于为实现传入定制参数
 * </pre>
 */
public interface JobInterval {

    /**
     * <pre>
     * 🔗 绑定控制函数
     *
     * 说明:
     * - 将一个 Consumer<Long> 绑定到调度器上，用来接收控制（例如停止/调整）指令
     * - 返回自身以便链式调用
     *
     * 参数说明:
     * controlFn: 接收 Long 类型的控制值（含义由实现方定义，例如 remaining time 或命令码）
     * </pre>
     */
    JobInterval bind(Consumer<Long> controlFn);

    /**
     * <pre>
     * ⚙️ 配置方法
     *
     * 说明:
     * - 为实现提供 JsonObject 配置入口
     * - 默认实现为空，子类可覆盖以读取必要配置项
     *
     * 参数说明:
     * config: 配置参数，以 JsonObject 形式传入
     * </pre>
     */
    default void configure(final JsonObject config) {
        // 引用参数以避免未使用的静态分析警告；默认实现不做任何处理
        if (config == null) {
            return;
        }
        // 访问一次以标记参数已被使用（无副作用读取）
        config.size();
    }

    /**
     * <pre>
     * 设计说明:
     * - 原始调度类型包括: FIXED、PLAN、ONCE
     * - 扩展以支持: MONTH、WEEK、QUARTER、YEAR
     * - 标准化调度阶段:
     *   1) Wait  等待下次触发时间
     *   2) Run   执行任务
     *   3) Repeat Or End 重复或结束
     *   4) Update KScheduler.runAt 更新下一次运行时间
     *
     * 开发模式说明:
     * - startAt(Handler<Long>) 为便捷开发调用，通常用于界面触发即时执行以调试任务
     * - 调用该方法后，任务会直接执行（无等待阶段）以便观察运行细节
     * </pre>
     */
    default void startAt(final Handler<Long> actuator) {
        this.startAt(actuator, null);
    }

    /**
     * <pre>
     * ▶️ 启动任务（带调度上下文）
     *
     * 说明:
     * - 在指定的 KScheduler 上启动任务
     * - 若 timer 为 null，则立即执行（等同于开发调试模式）
     *
     * 参数:
     * actuator: 任务执行回调，接收 Long 类型参数（语义由实现定义）
     * timer: 可选的 KScheduler，提供下一次运行时间与周期信息
     * </pre>
     */
    void startAt(Handler<Long> actuator, KScheduler timer);

    /**
     * <pre>
     * 🔄 重新启动任务（通常用于定时器更新后的再调度）
     *
     * 说明:
     * - 与 startAt 类似，但语义偏向重新调度/恢复
     * - 实现应保证在重复执行场景下状态一致性
     *
     * 参数:
     * actuator: 任务执行回调
     * timer: KScheduler 提供上下文信息
     * </pre>
     */
    void restartAt(Handler<Long> actuator, KScheduler timer);
}
