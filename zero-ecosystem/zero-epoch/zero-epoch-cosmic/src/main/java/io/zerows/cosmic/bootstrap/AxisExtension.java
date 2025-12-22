package io.zerows.cosmic.bootstrap;

import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.plugins.OAxisDynamicGateway;
import io.zerows.cosmic.plugins.OAxisGateway;
import io.zerows.cosmic.plugins.OAxisSockGateway;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class AxisExtension implements Axis {

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        // Websocket 功能
        try {
            final OAxisGateway sockGateway = OAxisGateway.of(OAxisSockGateway.class);
            final Axis sockAxis = sockGateway.getAxis(bundle);
            if (Objects.nonNull(sockAxis)) {
                sockAxis.mount(server, bundle);
            }
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
            System.exit(1);
        }

        // Dynamic 功能
        try {
            final OAxisGateway dynamicGateway = OAxisGateway.of(OAxisDynamicGateway.class);
            final Axis dynamicAxis = dynamicGateway.getAxis(bundle);
            if (Objects.nonNull(dynamicAxis)) {
                dynamicAxis.mount(server, bundle);
            }
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
            System.exit(1);
        }
    }
}
