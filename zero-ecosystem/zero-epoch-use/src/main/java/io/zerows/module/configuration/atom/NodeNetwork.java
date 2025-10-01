package io.zerows.module.configuration.atom;

import io.zerows.epoch.enums.app.ServerType;
import io.zerows.module.configuration.atom.option.ClusterOptions;
import io.zerows.specification.configuration.HSetting;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 配置管理专用结构，用于管理 Options，最终会以服务的方式转出去形成统一配置，一个节点
 * <pre><code>
 *     1. ClusterOptions 只有一个，交给 AdminCluster 管理
 *        所有的 NodeNetwork 有一个指向 ClusterOptions 的引用
 *     2. VertxOptions 可能存在多个
 *        name = VertxOptions 的结构，此处的 NodeNetwork 中就会包含多个 Vertx 配置
 *     3. DeploymentOptions 的数量在 VertxOptions 之下，根据配置的 Verticle 来定义
 *        DeliveryOptions 的数量和 VertxOptions 的数量和 EventBus 数量一致
 *     4. ServerOptions 的数量同样在 VertxOptions 之下，根据配置的 Server 来定义
 * </code></pre>
 * 所以此处会出现一种正交状态，而 NodeNetwork 做线性结构，最终可根据 key 直接计算，最终 NodeNetwork 为网络节点配置，每个网络节点只允许出
 * 现一个 Cluster，但是可以包含多个 {@link NodeVertx} 实例，内置包含了 Vertx 实例配置，在管理模式开启之后，这些节点配置都会被注册到同一
 * 个集群中，这样的模式下所有网络节点会配合操作执行一个 Cluster 级的管理，而且可横向扩展 Vertx 追加相关实例。
 *
 * @author lang : 2024-04-20
 */
public class NodeNetwork implements Serializable {

    private final ConcurrentMap<String, NodeVertx> vertxOptions = new ConcurrentHashMap<>();
    private volatile NodeVertx vertxNodeRef;
    // Cannot invoke "io.zerows.core.module.option.atom.configuration.ClusterOptions.isEnabled()" because "clusterOptions" is null
    private volatile ClusterOptions clusterOptions = new ClusterOptions();
    private volatile HSetting nodeSetting;

    public NodeNetwork() {
    }

    // 当前节点所属集群配置
    public NodeNetwork cluster(final ClusterOptions clusterOptions) {
        this.clusterOptions = clusterOptions;
        return this;
    }

    public ClusterOptions cluster() {
        return this.clusterOptions;
    }

    // 当前节点所属 Vertx 配置
    public NodeNetwork add(final String name, final NodeVertx vertxOptions) {
        if (Objects.isNull(this.vertxNodeRef)) {
            this.vertxNodeRef = vertxOptions;
        }
        this.vertxOptions.put(name, vertxOptions);
        return this;
    }

    public void remove(final String name) {
        this.vertxOptions.remove(name);
    }

    public NodeVertx get(final String name) {
        return this.vertxOptions.get(name);
    }

    public ConcurrentMap<String, NodeVertx> vertxOptions() {
        return this.vertxOptions;
    }

    /**
     * 准备就绪的整体的前提是已经包含了 Vertx 实例，Cluster 实例的存在与否不重要。
     *
     * @return 是否准备就绪
     */
    public boolean isReady() {
        return !this.vertxOptions().isEmpty();
    }

    /**
     * 位于最终调用流程中，在 io.zerows.core.module.zdk.configuration.Processor 中最后一步调用，此处选择最后调用此方法的原因有二：
     * <pre><code>
     *     1. 保证所有其他配置加载完成，即当前节点所有配置已经解析完成，则注入配置。
     *     2. （历史原因）最早没有将 HSetting 和 NodeNetwork 捆绑到一起。
     * </code></pre>
     *
     * @param setting 节点配置
     *
     * @return 当前节点
     */
    public NodeNetwork build(final HSetting setting) {
        this.nodeSetting = setting;
        return this;
    }

    public HSetting setting() {
        return this.nodeSetting;
    }
    // ------------------- 临时方案：默认单个实例

    public synchronized NodeVertx get() {
        return this.vertxNodeRef;
    }


    public boolean okRpc() {
        final Set<String> serverIpc = this.vertxNodeRef.optionServers(ServerType.IPC);
        return !serverIpc.isEmpty();
    }

    public boolean okSock() {
        final Set<String> serverSock = NodeNetwork.this.vertxNodeRef.optionServers(ServerType.SOCK);
        return !serverSock.isEmpty();
    }
}
