package io.zerows.plugins.monitor.server;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.monitor.metadata.MonitorType;
import io.zerows.plugins.monitor.metadata.YmMonitor;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

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
        final Supplier<MonitorJmxConnector> constructorFn = MonitorJmxUtil.SUPPLIER.getOrDefault(monitorType, null);
        if (Objects.isNull(constructorFn)) {
            return null;
        }

        return CC_JMX.pick(constructorFn, monitorType.name());
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


    Future<Boolean> startAsync(JsonObject config, Vertx vertxRef);
}
