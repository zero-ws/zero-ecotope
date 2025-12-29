package io.zerows.plugins.monitor.server;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.monitor.metadata.MonitorType;
import io.zerows.plugins.monitor.metadata.YmMonitor;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * 解析配置之后创建连接器，若成功启动则返回 true，此处连接器会包含监控组件部分
 * <pre>
 *     1. 配置对应如下
 *        - monitor-type
 *        - monitor-component
 *        - monitor-config
 *     2. 不同组件的选择的连接器实现不同，构造 {@link MonitorJmxConnector} 组件
 *        - monitor-type 已经配置        -> 原生（内部构造）
 *        - monitor-component           -> 优先级更高
 * </pre>
 *
 * @author lang : 2025-12-29
 */
public interface MonitorJmxConnector {

    Cc<String, MonitorJmxConnector> CC_JMX = Cc.open();

    private static MonitorJmxConnector ofType(final YmMonitor.Server server) {
        final MonitorType monitorType = server.getMonitorType();
        if (Objects.isNull(monitorType)) {
            return null;
        }
        final List<MonitorJmxConnector> foundList = HPI.findMany(MonitorJmxConnector.class);

        final List<MonitorJmxConnector> compress = foundList.stream()
            .filter(connector -> connector.isMatch(monitorType))
            .toList();
        if (compress.isEmpty()) {
            return null;
        } else if (1 == compress.size()) {
            return compress.getFirst();
        } else {
            /*
             * 多个实现类的查找模型，当同一个 MonitorType 存在多个实现类时，已经无法定位到核心的 Connector，这种模型下最好的方式就是
             * 统一查找优先级最高的唯一实现类！所以此处会诱发一个拉平场景
             * 1）如果想要多个实现类共存：3 个不同的 MonitorType 都提供唯一实现
             * 2）如果只想要三选一：则只能保证某一个实现类的优先级最高
             */
            return CC_JMX.pick(() -> HPI.findOneOf(MonitorJmxConnector.class), monitorType.name());
        }
    }

    static MonitorJmxConnector of(final YmMonitor.Server server) {
        final Class<?> componentCls = server.getMonitorComponent();
        if (Objects.isNull(componentCls)) {
            return ofType(server);
        }
        final String componentName = componentCls.getName();
        if (!Ut.isImplement(componentCls, MonitorJmxConnector.class)) {
            final Logger logger = LoggerFactory.getLogger(componentCls);
            logger.warn("[ MNTR ] 自定义监控组件 `{}` 不合法，切换默认流程……", componentName);
            return ofType(server);
        }
        return CC_JMX.pick(() -> SourceReflect.instance(componentCls), componentName);
    }

    boolean isMatch(MonitorType required);

    Future<Boolean> startAsync(JsonObject config, Vertx vertxRef);
}
