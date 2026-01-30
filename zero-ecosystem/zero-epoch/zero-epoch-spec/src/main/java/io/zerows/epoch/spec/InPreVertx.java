package io.zerows.epoch.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 🚀 Vert.x 引导配置核心定义 (Bootstrap Configuration Core)
 *
 * <p>
 * 该类映射自 {@code vertx-boot.yml} 中的 {@code vertx} 根节点。
 * 它是整个系统启动流程的<b>分水岭 (Pivot Point)</b>，采用<b>“多选一”</b>的策略决定系统的配置源。
 * </p>
 *
 * <pre>
 * ⚖️ 选择策略 (Selection Strategy):
 * 系统严格遵循 <b>Single Source of Truth (SSOT)</b> 原则。
 * 即使 {@link #cloud} 容器中同时定义了 Nacos、Zookeeper 等多份配置，
 * 系统在启动时<b>只会</b>初始化 {@link #selected} 指定的那一个 {@code ConfigProvider}。
 * 其他未被选中的配置将被忽略，从而避免多源并存导致的“脑裂”或优先级覆盖混乱。
 * </pre>
 *
 * @author lang : 2025-10-06
 */
@Data
public class InPreVertx implements Serializable {

    /**
     * 🎯 唯一激活的云端组件 (The Selected Config Provider)
     *
     * <p>
     * 这是一个<b>排他性 (Exclusive)</b> 的选择开关。
     * 它指定了在 {@link #cloud} 容器中，哪一个组件被激活作为当前的配置服务提供者 (ConfigProvider)。
     * 它的值必须与 {@link YmCloud} 中动态 Map 的 Key 严格匹配。
     * </p>
     *
     * <pre>
     * 🔘 选项示例 (Options):
     * - "nacos"     : (默认) 仅激活 Nacos Provider，忽略其他。
     * - "zookeeper" : 仅激活 Zookeeper Provider，忽略其他。
     * - "etcd"      : 仅激活 Etcd Provider，忽略其他。
     *
     * ⚠️ 注意：
     * 修改此值将直接改变系统底层的 ConfigProvider 实现类。
     * </pre>
     */
    private String selected = "nacos";

    /**
     * ☁️ 云端集成配置容器 (Cloud Integration Container)
     *
     * <p>
     * 一个宽容的配置仓库，存放所有潜在的云端组件连接信息（如 Nacos 的地址、ZK 的集群串）。
     * 虽然这里可以容纳多种配置，但<b>只有</b>被 {@link #selected} 选中的那一份配置会被读取和使用。
     * </p>
     */
    private YmCloud cloud;

    /**
     * ⚙️ 核心配置行为 (Core Configuration Behavior)
     *
     * <p>
     * 定义配置系统的加载行为，例如是否启用远程配置导入 (Import)。
     * </p>
     */
    private Config config;

    /**
     * 🆔 应用基础元数据 (Application Metadata)
     *
     * <p>
     * 包含应用名称、部署 ID 等基础标识信息。
     * </p>
     */
    private YmApplication application = new YmApplication();

    /**
     * 📦 导入规则定义 (Import Rules)
     *
     * <p>
     * 定义了应用启动时需要从配置中心加载哪些 Data ID 或 Group。
     * 具体的加载逻辑由当前激活的 {@code ConfigProvider} 实现。
     * </p>
     */
    @Data
    public static class Config implements Serializable {
        private static final String KEY_IMPORT = "import";

        /**
         * 📥 导入列表 (Import List)
         *
         * <p>
         * 指定需要加载的远程配置资源标识列表。
         * </p>
         *
         * <pre>
         * 🌰 YAML 示例:
         * vertx:
         * config:
         * import:
         * - "optional:nacos:shared-config.yaml"
         * - "nacos:${vertx.application.name}.yaml"
         * </pre>
         */
        @JsonProperty(KEY_IMPORT)
        private List<String> imports = new ArrayList<>();
    }
}