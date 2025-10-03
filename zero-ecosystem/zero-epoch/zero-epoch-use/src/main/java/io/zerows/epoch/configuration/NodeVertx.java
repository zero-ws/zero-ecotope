package io.zerows.epoch.configuration;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.configuration.option.ActorTool;
import io.zerows.epoch.configuration.option.RpcOptions;
import io.zerows.epoch.configuration.option.SockOptions;
import io.zerows.epoch.configuration.server.OptionBuilder;
import io.zerows.platform.enums.EmDeploy;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.epoch.sdk.environment.OptionOfServer;
import io.zerows.specification.configuration.HSetting;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Vertx 专用节点，清单
 * <pre><code>
 *     1. VertxOptions 一个
 *     2. DeploymentOptions 三个
 *        - 默认的 Agent
 *        - 默认的 Worker
 *        - 默认的 Scheduler
 *     3. DeliveryOptions 一个
 *     4. ServerOptions N 个
 * </code></pre>
 *
 * @author lang : 2024-04-20
 */
public class NodeVertx implements Serializable {
    @SuppressWarnings("all")
    private final ConcurrentMap<String, OptionOfServer> serverOptions = new ConcurrentHashMap<>();
    /**
     * 为了同时兼容底层和上层处理，此处不考虑使用 Class<?> 作键值，由于要实现动态部署流程，Class 的信息有可能在配置中心
     * 上线之前并没有加载到环境中，所以此处的 {@link DeploymentOptions} 直接使用类名来配置，这样就可以保证配置的延迟性，
     * 使得配置本身不会受到元数据的影响。
     */
    private final ConcurrentMap<String, DeploymentOptions> deploymentOptions =
        new ConcurrentHashMap<>();
    private final String vertxName;
    private final NodeNetwork vertxNetwork;

    private EmDeploy.Mode mode = EmDeploy.Mode.CONFIG;
    private VertxOptions vertxOptions;
    private DeliveryOptions deliveryOptions;

    private NodeVertx(final String vertxName, final NodeNetwork networkRef) {
        this.vertxName = vertxName;
        this.vertxNetwork = networkRef;
    }

    public static NodeVertx of(final String vertxName, final NodeNetwork networkRef) {
        return new NodeVertx(vertxName, networkRef);
    }

    public String name() {
        return this.vertxName;
    }

    public NodeNetwork belongTo() {
        return this.vertxNetwork;
    }

    public void mode(final EmDeploy.Mode mode) {
        this.mode = mode;
    }

    public void optionVertx(final VertxOptions vertxOptions) {
        this.vertxOptions = vertxOptions;
    }

    public VertxOptions optionVertx() {
        return this.vertxOptions;
    }

    public void optionDeployment(final String className,
                                 final DeploymentOptions deploymentOptions) {
        this.deploymentOptions.put(className, deploymentOptions);
    }

    public HSetting setting() {
        return this.vertxNetwork.setting();
    }

    /**
     * 这个方法拥有特殊的业务逻辑，会根据 Class 中的注解来修订 {@link DeploymentOptions}
     *
     * @param component 传入的 Class 类型
     *
     * @return {@link DeploymentOptions}
     */
    public DeploymentOptions optionDeployment(final Class<?> component) {
        Objects.requireNonNull(component);
        final DeploymentOptions options = this.deploymentOptions.get(component.getName());
        if (Objects.nonNull(options)) {
            ActorTool.setupWith(options, component, this.mode);
            // 反向更新
            this.deploymentOptions.put(component.getName(), options);
        }
        return options;
    }

    public void optionDelivery(final DeliveryOptions deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    public DeliveryOptions optionDelivery() {
        return this.deliveryOptions;
    }

    // 当前节点所属 Server 配置
    public void optionServer(final String name,
                             final ServerType type,
                             final HttpServerOptions serverOptions) {
        this.serverOptions.put(name, OptionBuilder.ofHttp(name, type, serverOptions));
    }

    public void optionServer(final String name, final RpcOptions serverOptions) {
        this.serverOptions.put(name, OptionBuilder.ofRpc(name, serverOptions));
    }

    public void optionServer(final String name, final SockOptions serverOptions) {
        this.serverOptions.put(name, OptionBuilder.ofSock(name, serverOptions));
    }

    @SuppressWarnings("all")
    public <T> OptionOfServer<T> optionServer(final String name) {
        return (OptionOfServer<T>) this.serverOptions.get(name);
    }

    public Set<String> optionServers(final ServerType type) {
        if (Objects.isNull(type)) {
            return this.serverOptions.keySet();
        } else {
            final Set<String> servers = new HashSet<>();
            this.serverOptions.forEach((serverName, option) -> {
                if (type == option.type()) {
                    servers.add(serverName);
                }
            });
            return servers;
        }
    }

    public NodeVertx build() {
        // 最终绑定
        final Set<String> socks = this.optionServers(ServerType.SOCK);
        socks.stream().map(this::<SockOptions>optionServer).forEach(optionOfSock -> {
            final SockOptions optionSock = optionOfSock.options();
            final HttpServerOptions sockHttp = optionSock.options();
            final HttpServerOptions httpSock = optionSock.options();

            final HttpServerOptions bridgeOptions = this.findBy(httpSock.getHost(), httpSock.getPort());
            if (Objects.nonNull(bridgeOptions)) {
                /*
                 * Fix: WebSocket 无法触发的问题解决，此处的特殊逻辑流程如下
                 * 1. 从 SockOptions 中提取配置信息，配置位于 vertx-server.yml
                 *    - name: ht-ws
                 *      type: sock
                 *      config:
                 *         port: 7085
                 *         webSocketSubProtocols:
                 *           - v12.stomp
                 *           - v11.stomp
                 *           - v10.stomp
                 * 2. 提取 optionSock 中的 HttpServerOptions，并且拷贝它的内容到 bridgeOptions 中，如果匹配的情况下直接追加
                 *    WebSocket 配置到 bridgeOptions 中
                 * 3. 然后使用最新的 bridgeOptions 作为 optionSock 的桥接服务器配置，如此可完成对应的桥接处理，否则会导致
                 *    SockOption 中桥接的配置没有任何和 WebSocket 相关的配置信息而使得 WebSocket 无法开启
                 */
                // 一定要先做桥接处理
                optionSock.options(bridgeOptions);
                // 再将桥接设置配置到 optionOfSock 中
                optionOfSock.serverBridge(optionSock.options());
            } else {
                // 维持原状
                optionSock.options(sockHttp);
            }
        });
        return this;
    }

    private HttpServerOptions findBy(final String host, final int port) {
        final Set<String> https = this.optionServers(ServerType.HTTP);
        return https.stream().map(this::<HttpServerOptions>optionServer).filter((optionOfHttp) -> {
            if (Objects.isNull(optionOfHttp.options())) {
                return false;
            }
            final HttpServerOptions bridgeOptions = optionOfHttp.options();
            // 0.0.0.0
            if (HttpServerOptions.DEFAULT_HOST.equals(host) ||
                HttpServerOptions.DEFAULT_HOST.equals(bridgeOptions.getHost())) {
                // 只检查端口
                return port == bridgeOptions.getPort();
            } else {
                // 同时检查 Host 和端口
                return port == bridgeOptions.getPort()
                    && host.equals(bridgeOptions.getHost());
            }
        }).map(OptionOfServer::options).findFirst().orElse(null);
    }

    /*
     * 检索 WebSocket 的专用方法，用于反向查找，因为 WebSocket 和 HTTP 可以共享 Host 和 Port
     */
    public OptionOfServer<SockOptions> findSock(final OptionOfServer<HttpServerOptions> optionHttp) {
        final Set<String> socks = this.optionServers(ServerType.SOCK);
        return socks.stream().map(this::<SockOptions>optionServer).filter((optionOfSock) -> {
            final HttpServerOptions bridgeOptions = optionOfSock.serverBridge();
            if (Objects.isNull(bridgeOptions)) {
                return false;
            }
            final HttpServerOptions httpOptions = optionHttp.options();
            // 0.0.0.0
            if (HttpServerOptions.DEFAULT_HOST.equals(httpOptions.getHost()) ||
                HttpServerOptions.DEFAULT_HOST.equals(bridgeOptions.getHost())) {
                // 只检查端口
                return httpOptions.getPort() == bridgeOptions.getPort();
            } else {
                // 同时检查 Host 和端口
                return httpOptions.getPort() == bridgeOptions.getPort()
                    && httpOptions.getHost().equals(bridgeOptions.getHost());
            }
        }).findFirst().orElse(null);
    }
}
