package io.zerows.epoch.corpus.container.uca.routing;

import io.zerows.epoch.corpus.container.uca.plugins.OAxisDynamicGateway;
import io.zerows.epoch.corpus.container.uca.plugins.OAxisGateway;
import io.zerows.epoch.corpus.container.uca.plugins.OAxisSockGateway;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.model.running.RunServer;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * 扩展路由插件，用于处理扩展路由，扩展路由主要包含两部分路由处理
 * <pre><code>
 *     1. WebSocket 路由
 *     2. Dynamic 动态路由
 * </code></pre>
 * 引入新的 Manager 结构来构造不同路由中的 Manager 信息
 * <pre><code>
 *     1. 非 OSGI 环境中直接从 SPI 中提取
 *     2. OSGI 环境中走 Service 服务提取
 * </code></pre>
 *
 * @author lang : 2024-06-26
 */
public class AxisExtension implements OAxis {

    @Override
    public void mount(final RunServer server, final Bundle bundle) {
        // Websocket 功能
        final OAxisGateway sockGateway = OAxisGateway.of(OAxisSockGateway.class);
        final OAxis sockAxis = sockGateway.getAxis(bundle);
        if (Objects.nonNull(sockAxis)) {
            sockAxis.mount(server, bundle);
        }


        // Dynamic 动态扩展
        final OAxisGateway dynamicGateway = OAxisGateway.of(OAxisDynamicGateway.class);
        final OAxis dynamicAxis = dynamicGateway.getAxis(bundle);
        if (Objects.nonNull(dynamicAxis)) {
            dynamicAxis.mount(server, bundle);
        }
    }
}
