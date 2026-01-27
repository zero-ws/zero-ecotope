package io.zerows.epoch.spec;

import io.vertx.core.json.JsonObject;

/**
 * 🏗️ 多层级配置融合标准接口 (Multi-Layer Configuration Fusion Standard)
 * <p>
 * 定义了在微服务启动或动态刷新过程中，不同来源（Source）的配置数据如何进行加权融合。
 * 核心设计目标是实现 "洋葱圈" 式的配置加载模型，确保高优先级配置（如环境变数、云端配置）能够精准覆盖低优先级配置（如本地默认值）。
 *
 * <pre>
 * ⚔️ 优先级阶梯 (Priority Ladder) - [由低到高]:
 * 1. 🟢 Hardcode Defaults  : 代码中硬编码的默认兜底值
 * 2. 🔵 Local File (YAML)  : 本地 classpath 下的静态配置文件
 * 3. 🟠 Cloud (Nacos/Etcd) : 远程配置中心拉取的动态配置
 * 4. 🔴 CLI / ENV Args     : 启动参数或操作系统环境变量 (最高权重)
 *
 * 🧬 融合行为细节 (Merge Behavior):
 * - Object (Map) : 执行递归深度合并 (Recursive Deep Merge)。
 * - Array (List) : 执行完全替换 (Replace)，即高优先级的 List 会完全覆盖低优先级的 List，而非追加。
 * - Primitive    : 执行值覆盖 (Override)，以后加载的值为准。
 *
 * 🌰 融合示例 (Example):
 * Source A (Local): { "server": { "port": 8080, "host": "localhost" }, "whitelist": ["127.0.0.1"] }
 * Source B (Cloud): { "server": { "port": 9000 },                      "whitelist": ["192.168.0.1"] }
 * ---------------------------------------------------------------------------------------------------
 * Result (Final)  : { "server": { "port": 9000, "host": "localhost" }, "whitelist": ["192.168.0.1"] }
 * </pre>
 *
 * @author lang : 2025-10-09
 */
public interface YmPriority {

    /**
     * 🚀 获取加权融合后的最终配置视图
     * <p>
     * 触发配置计算逻辑，将当前上下文中所有激活的配置源按照 {@code Low -> High} 的顺序压栈合并。
     * 实现类应当保证该操作的幂等性，并处理好配置源缺失（Null Safety）的情况。
     *
     * <pre>
     * ⚙️ 处理逻辑 (Processing Logic):
     * 1. 初始化空 JsonObject 容器。
     * 2. 遍历所有配置层级 (Layer)。
     * 3. 若某层级数据不为空，调用 {@code container.mergeIn(layer, true)}。
     * 4. 返回最终不可变或深拷贝的配置对象。
     *
     * ⚠️ 注意事项:
     * - 此方法可能涉及耗时的计算或 I/O 操作 (视实现而定)，但在 Boot 阶段通常容许同步执行。
     * - 返回的 JsonObject 应当被视为 Read-Only，避免修改影响全局状态。
     * </pre>
     *
     * @return {@link JsonObject} 融合后的最终配置数据；若无任何有效配置，应返回空对象而非 null
     */
    default JsonObject combined() {
        return null;
    }
}