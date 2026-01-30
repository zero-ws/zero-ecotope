package io.zerows.epoch.jigsaw;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.spec.options.ClusterOptions;
import io.zerows.epoch.spec.options.SockOptions;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.HLog;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 🌐 网络节点配置管理器
 *
 * <p>用于管理网络节点的完整配置体系，采用分层结构管理不同类型的配置选项。核心设计模式为：</p>
 *
 * <pre><code>
 *     🏗️ 配置层级结构：
 *     1. 🌟 ClusterOptions 只有一个，所有 NodeNetwork 共享一个集群配置引用
 *     2. ⚡ VertxOptions 可能存在多个
 *        name = VertxOptions 的结构，NodeNetwork 包含多个 Vertx 配置实例
 *     3. 🚀 DeploymentOptions 的数量在 VertxOptions 之下，根据配置的 Verticle 数量定义
 *        DeliveryOptions 的数量和 VertxOptions 的数量和 EventBus 数量一致
 * </code></pre>
 *
 * <h2>🎯 设计目标</h2>
 * <p>形成正交状态的线性结构，每个网络节点只允许出现一个 Cluster，但可以包含多个 {@link NodeVertx} 实例，
 * 内置包含 Vertx 实例配置。管理模式开启后，所有节点配置都会注册到同一个集群中，形成 Cluster 级的统一管理，
 * 支持横向扩展 Vertx 实例。</p>
 *
 * @author lang : 2024-04-20
 */
@Slf4j
public class NodeNetwork implements Serializable, HLog {

    /**
     * 📦 Vertx 配置映射表
     * 存储不同名称对应的 Vertx 节点配置，支持多实例管理
     */
    private final ConcurrentMap<String, NodeVertx> vertxOptions = new ConcurrentHashMap<>();

    /**
     * 🌟 集群配置
     * 当前节点所属的集群配置信息
     */
    private volatile ClusterOptions clusterOptions;

    /**
     * 🌐 服务器配置
     * HTTP 服务器的配置选项
     */
    private volatile HttpServerOptions serverOptions;

    /**
     * 🔌 Socket 配置
     * WebSocket 等 Socket 连接的配置选项
     */
    private volatile SockOptions sockOptions;

    /**
     * ⚙️ 系统配置引用
     * 通用系统配置设置
     */
    private HSetting setting;

    /**
     * 🏗️ 构造函数
     * 创建空的网络节点配置实例
     */
    public NodeNetwork() {
    }

    // ============ 🌟 集群配置区域 ============

    /**
     * 🏗️ 设置集群配置
     * 为当前网络节点配置集群选项
     *
     * @param clusterOptions 集群配置对象
     * @return 当前网络节点配置实例（链式调用）
     */
    public NodeNetwork cluster(final ClusterOptions clusterOptions) {
        this.clusterOptions = clusterOptions;
        return this;
    }

    /**
     * 🌟 获取集群配置
     * 返回当前节点的集群配置信息
     *
     * @return 集群配置对象
     */
    public ClusterOptions cluster() {
        return this.clusterOptions;
    }

    // ============ 🌐 服务器配置区域 ============

    /**
     * 🏗️ 设置服务器配置
     * 配置 HTTP 服务器的相关选项
     *
     * @param serverOptions HTTP 服务器配置对象
     * @return 当前网络节点配置实例（链式调用）
     */
    public NodeNetwork server(final HttpServerOptions serverOptions) {
        this.serverOptions = serverOptions;
        return this;
    }

    /**
     * 🌐 获取服务器配置
     * 返回当前节点的 HTTP 服务器配置
     *
     * @return HTTP 服务器配置对象
     */
    public HttpServerOptions server() {
        return this.serverOptions;
    }

    /**
     * 🔌 设置 Socket 配置
     * 配置 WebSocket 等 Socket 连接选项
     *
     * @param sockOptions Socket 配置对象
     * @return 当前网络节点配置实例（链式调用）
     */
    public NodeNetwork sock(final SockOptions sockOptions) {
        this.sockOptions = sockOptions;
        return this;
    }

    /**
     * 🔌 获取 Socket 配置
     * 返回当前节点的 Socket 配置信息
     *
     * @return Socket 配置对象
     */
    public SockOptions sock() {
        return this.sockOptions;
    }

    // ============ 🚀 节点配置区域 ============

    /**
     * ➕ 添加 Vertx 节点配置
     * 向当前网络节点添加指定名称的 Vertx 配置
     *
     * @param name         配置名称标识
     * @param vertxOptions Vertx 节点配置对象
     * @return 当前网络节点配置实例（链式调用）
     */
    public NodeNetwork add(final String name, final NodeVertx vertxOptions) {
        this.vertxOptions.put(name, vertxOptions);
        return this;
    }

    /**
     * ❌ 移除 Vertx 节点配置
     * 从当前网络节点移除指定名称的 Vertx 配置
     *
     * @param name 配置名称标识
     */
    public void remove(final String name) {
        this.vertxOptions.remove(name);
    }

    /**
     * 🎯 获取指定名称的 Vertx 节点配置
     * 根据名称获取对应的 Vertx 配置对象
     *
     * @param name 配置名称标识
     * @return Vertx 节点配置对象，不存在则返回 null
     */
    public NodeVertx get(final String name) {
        return this.vertxOptions.get(name);
    }

    /**
     * 📋 获取所有 Vertx 节点配置映射
     * 返回当前网络节点包含的所有 Vertx 配置映射表
     *
     * @return Vertx 配置映射表
     */
    public ConcurrentMap<String, NodeVertx> vertxNodes() {
        return this.vertxOptions;
    }

    public boolean isOk() {
        return !this.vertxOptions.isEmpty();
    }

    // ============ ⚙️ 系统配置引用区域 ============

    /**
     * ⚙️ 获取系统配置
     * 返回当前网络节点的系统配置引用
     *
     * @return 系统配置对象
     */
    public HSetting setting() {
        return this.setting;
    }

    /**
     * ⚙️ 设置系统配置
     * 为当前网络节点配置系统设置
     *
     * @param setting 系统配置对象
     * @return 当前网络节点配置实例（链式调用）
     */
    public NodeNetwork setting(final HSetting setting) {
        this.setting = setting;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public NodeNetwork vLog() {
        final StringBuilder content = new StringBuilder();
        content.append("[ ZERO ] Network/Vertx 配置：\n");
        content.append("\t 集群配置：")
            .append(Objects.isNull(this.clusterOptions) ? null : this.clusterOptions.getOptions()).append("\n");
        content.append("\t 服务器配置：\n\t\t 域名(IP): ").append(this.serverOptions.getHost()).append("\n")
            .append("\t\t 端口: ").append(this.serverOptions.getPort()).append("\n");
        content.append("\t WebSocket 配置：")
            .append(Objects.isNull(this.sockOptions) ? null : this.sockOptions.getConfig()).append("\n");
        int index = 0;
        for (final String name : this.vertxOptions.keySet()) {
            final NodeVertx nodeOptions = this.vertxOptions.get(name);
            content.append("\t [").append(index).append("] Vertx 实例: \uD83E\uDDEA ").append(name).append("\n");
            content.append("\t\t Vertx 配置: ").append(nodeOptions.vertxOptions()).append("\n");
            content.append("\t\t Agent 默认配置: ").append(nodeOptions.agentOptions().toJson()).append("\n");
            content.append("\t\t Worker 默认配置: ").append(nodeOptions.workerOptions().toJson()).append("\n");
            index++;
        }
        log.info(content.toString());
        return this;
    }
}