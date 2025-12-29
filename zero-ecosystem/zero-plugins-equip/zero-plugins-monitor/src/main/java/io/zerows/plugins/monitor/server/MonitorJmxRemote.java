package io.zerows.plugins.monitor.server;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.plugins.monitor.metadata.YmMonitor;
import lombok.extern.slf4j.Slf4j;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

/**
 * 远程监控处理，只要包含记录 jmx-host 配置则可视为远程监控处理
 * <p>
 * 负责启动 JMX RMI Connector，允许外部工具（如 VisualVM, JConsole）通过 TCP 连接。
 * </p>
 */
@Slf4j
public class MonitorJmxRemote {

    private static final Cc<String, MonitorJmxRemote> CC_REMOTE = Cc.open();
    private final YmMonitor.Server serverConfig;

    // 持有引用，虽然本例只涉及 start，但在完整生命周期管理中通常需要用于 stop
    private JMXConnectorServer jmxConnectorServer;

    private MonitorJmxRemote(final YmMonitor.Server serverConfig) {
        this.serverConfig = serverConfig;
    }

    public static MonitorJmxRemote of(final YmMonitor.Server serverConfig) {
        // 假定外层已校验 serverConfig 非空
        // 使用配置的哈希值作为缓存键，确保相同配置只创建一个实例
        return CC_REMOTE.pick(() -> new MonitorJmxRemote(serverConfig), String.valueOf(serverConfig.hashCode()));
    }

    public void start(final Vertx vertxRef) {
        final Integer port = this.serverConfig.getJmxPort();

        // 1. 业务检查：如果没有配置端口，或者端口非法，则不启动远程连接器
        // (注：serverConfig 对象本身合法，不代表端口一定开启，所以此处保留业务逻辑判断)
        if (port == null || port <= 0) {
            log.info("[ MNTR ] 远程 JMX 端口配置无效或未启用 (port={})，跳过启动。", port);
            return;
        }

        // 2. 使用 executeBlocking 防止阻塞 EventLoop 线程 (适配 Vert.x 5.0 写法)
        vertxRef.executeBlocking(() -> {
            // 如果已经启动过，避免重复启动，直接返回成功
            if (this.jmxConnectorServer != null && this.jmxConnectorServer.isActive()) {
                return null;
            }

            final String host = this.serverConfig.getJmxHost();
            // 如果 host 为空，默认为 localhost；如果在容器内需要外部访问，通常配置为 0.0.0.0
            final String hostname = (host == null || host.trim().isEmpty()) ? "localhost" : host;

            log.info("[ MNTR ] 正在启动 JMX 远程连接器，监听地址：{}:{} ...", hostname, port);

            // 3. 启动 RMI Registry (JMX 依赖项)
            try {
                LocateRegistry.createRegistry(port);
            } catch (final Exception e) {
                // 这种异常通常是因为 Registry 已经在该端口运行（可能是同一个JVM的其他服务），尝试继续
                log.warn("[ MNTR ] 检测到 RMI 注册表可能已在端口 {} 上运行，尝试复用。错误信息：{}", port, e.getMessage());
            }

            // 4. 构建 JMX Service URL
            // 格式: service:jmx:rmi://{host}:{port}/jndi/rmi://{host}:{port}/jmxrmi
            // 这种双端口写法是为了解决 RMI 动态端口分配问题，强制 Data Port 和 Registry Port 一致，方便穿透防火墙
            final String urlStr = String.format("service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi",
                hostname, port, hostname, port);
            final JMXServiceURL url = new JMXServiceURL(urlStr);

            // 5. 获取 Platform MBean Server (Vert.x Metrics 已经注册在此)
            final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            // 6. 创建并启动 Connector Server
            this.jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
            this.jmxConnectorServer.start();

            log.info("[ MNTR ] JMX 远程连接器启动成功，URL：{}", urlStr);
            return null; // Callable 必须有返回值

        }).onComplete(res -> {
            if (res.failed()) {
                log.error("[ MNTR ] JMX 远程连接器启动失败", res.cause());
            }
        });
    }
}