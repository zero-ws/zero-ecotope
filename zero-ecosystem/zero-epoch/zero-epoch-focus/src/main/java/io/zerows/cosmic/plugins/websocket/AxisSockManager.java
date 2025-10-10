package io.zerows.cosmic.plugins.websocket;

import io.zerows.cortex.AxisSockFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.management.OCacheNode;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-06-26
 */
public class AxisSockManager implements AxisSockFactory {

    @Override
    public Axis getAxis() {
        return Axis.ofOr(AxisWs.class);
    }

    @Override
    public boolean isEnabled(final HBundle owner) {
        // 访问 NodeNetwork 提取基础配置信息
        final NodeNetwork network = OCacheNode.of(owner).network();
        return false; // network.okSock();
    }
}
