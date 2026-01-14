package io.zerows.cosmic.plugins.job;

import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.Mission;

import java.util.Set;

/**
 * <pre>
 * 📦 JobStore — 任务存储桥接接口
 *
 * 说明:
 * 1. 提供框架内任务定义的统一抽象，用于获取、添加、更新与删除任务（Set<Mission>）
 * 2. 支持从编程注解(@Job)与外部存储（如数据库/配置文件）两类来源加载任务
 * 3. 实现方可用于将任务同步到运行时 JobPool 或暴露给外部管理控制台
 *
 * 主要职责与特性:
 * - fetch(): 获取当前可用的任务集合（合并编程定义与持久化定义）
 * - fetch(code): 按 code 查询单个任务定义
 * - add/update/remove: 对任务集合执行增、改、删操作，并返回 JobStore 以支持链式调用
 * - configure(JsonObject): 可选的初始化配置入口，供实现方读取运行时参数
 *
 * 注意事项:
 * 🔒 实现方应保证线程安全与幂等性
 * 🔁 add/update/remove 的默认行为应考虑在无扩展实现时安全退化
 * </pre>
 */
public interface JobStore {

    default void initialize() {
        
    }

    /**
     * <pre>
     * ⚙️ 配置方法（可选）
     *
     * 说明:
     * - 为实现方提供 JsonObject 配置入口（例如从 vertx-job.yml 读取配置）
     * - 默认实现不做任何处理；实现方可覆盖以读取自定义选项
     *
     * 参数:
     * options: 配置对象（可能为 null）
     * </pre>
     */
    default void configure(final JsonObject options) {
        // 引用参数以避免未使用的静态分析警告；默认实现不做任何处理
        if (options == null) {
            return;
        }
        options.size();
    }

    /**
     * <pre>
     * 🔍 获取任务集合
     *
     * 说明:
     * - 返回框架当前可见的所有任务定义，通常是编程注解任务与持久化任务的合并集合
     * - 返回值为 Set<Mission>，调用者可将其同步到 JobPool
     * </pre>
     */
    Set<Mission> fetch();

    /**
     * <pre>
     * 🧭 按 code 查询单个任务
     *
     * 说明:
     * - 根据任务 code 精确查找对应的 Mission 定义
     * - 若不存在则返回 null
     *
     * 参数:
     * code: 任务唯一标识
     * </pre>
     */
    Mission fetch(String code);

    /**
     * <pre>
     * ❌ 从任务集合中移除指定任务
     *
     * 说明:
     * - 将 mission 从运行时任务集合中移除
     * - 返回 JobStore 以便链式调用或继续委托到具体实现
     *
     * 参数:
     * mission: 要移除的任务实例
     * </pre>
     */
    JobStore remove(Mission mission);

    /**
     * <pre>
     * 🔁 更新任务定义
     *
     * 说明:
     * - 将 mission 的新定义应用到运行时任务集合或持久化层
     * - 返回 JobStore 以便链式调用
     *
     * 参数:
     * mission: 包含新值的任务实例
     * </pre>
     */
    JobStore update(Mission mission);

    /**
     * <pre>
     * ➕ 添加新任务到集合
     *
     * 说明:
     * - 将新的 mission 插入到任务集合中，必要时同步到持久化层
     * - 返回 JobStore 以支持链式调用
     *
     * 参数:
     * mission: 新增的任务实例
     * </pre>
     */
    JobStore add(Mission mission);
}
