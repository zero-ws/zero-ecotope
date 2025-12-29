package io.zerows.plugins.monitor;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.plugins.monitor.metadata.YmMonitor;
import io.zerows.plugins.monitor.server.MonitorJmxConnector;
import io.zerows.plugins.monitor.server.MonitorJmxRemote;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-12-29
 */
@Actor(value = "monitor")
@Slf4j
public class MonitorActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        this.vLog("[ Monitor ] MonitorActor 初始化完成，启动中……");
        if (Objects.isNull(config)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        final YmMonitor monitorConfig = config.options(YmMonitor.class);
        if (Objects.isNull(monitorConfig)) {
            return Future.succeededFuture(Boolean.TRUE);
        }

        MonitorManager.of().configYaml(vertxRef, monitorConfig);
        this.vLog("[ Monitor ] MonitorActor 注册监控配置完成，配置：{}", config.options());


        // 启动 JMX 远程监控
        this.startJmxRemote(monitorConfig, vertxRef);


        // 启动 Server 部分
        final YmMonitor.Server serverConfig = monitorConfig.getServer();
        final MonitorJmxConnector connector = MonitorJmxConnector.of(serverConfig);
        if (Objects.isNull(connector)) {
            this.vLog("[ Monitor ] 无 JmxConnector 组件，直接返回！");
            return Future.succeededFuture(Boolean.TRUE);
        }
        return connector.startAsync(serverConfig.getMonitorConfig(), vertxRef)


            // Quota 启动
            .compose(started -> QuotaMonitor.of(vertxRef).startQuota(monitorConfig));
    }

    /**
     * 启动 JMX 远程连接器
     * <p>
     * 执行严格校验：
     * 1. 配置节点必须存在
     * 2. jmx-port 必须 > 0
     * 3. jmx-host 必须显式配置 (不为空)
     * </p>
     *
     * @param monitor  解析后的监控配置对象
     * @param vertxRef Vertx 引用
     */
    private void startJmxRemote(final YmMonitor monitor, final Vertx vertxRef) {
        // 1. 对象级校验
        if (Objects.isNull(monitor) || Objects.isNull(monitor.getServer())) {
            this.vLog("[ Monitor ] 监控配置(YmMonitor)或服务端配置(Server)为空，跳过 JMX 远程服务启动。");
            return;
        }

        final YmMonitor.Server server = monitor.getServer();

        // 2. 端口合法性校验
        final Integer port = server.getJmxPort();
        if (Objects.isNull(port) || port <= 0) {
            this.vLog("[ Monitor ] JMX 远程端口(jmx-port)未配置或无效(port={})，跳过启动。", port);
            return;
        }

        // 3. Host 显式配置校验
        final String host = server.getJmxHost();
        if (Objects.isNull(host) || host.trim().isEmpty()) {
            this.vLog("[ Monitor ] JMX 远程主机(jmx-host)未配置，跳过启动（远程连接需显式指定 Host）。");
            return;
        }

        // 4. 校验通过，委托给组件启动
        this.vLog("[ Monitor ] JMX 远程配置校验通过 (Host: {}, Port: {})，准备启动...", host, port);
        MonitorJmxRemote.of(server).start(vertxRef);
    }
}
