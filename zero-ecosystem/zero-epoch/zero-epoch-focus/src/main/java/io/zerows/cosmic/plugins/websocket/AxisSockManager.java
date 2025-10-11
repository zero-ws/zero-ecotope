package io.zerows.cosmic.plugins.websocket;

import io.zerows.cortex.AxisSockFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

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
        final NodeNetwork network = NodeStore.ofNetwork();
        return Objects.nonNull(network.sock());
    }
}
