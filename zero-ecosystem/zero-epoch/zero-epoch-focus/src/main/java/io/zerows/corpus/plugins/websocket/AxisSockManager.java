package io.zerows.corpus.plugins.websocket;

import io.zerows.epoch.basicore.NodeNetwork;
import io.zerows.epoch.corpus.io.management.AxisSockFactory;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.management.OCacheNode;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-06-26
 */
public class AxisSockManager implements AxisSockFactory {

    @Override
    public OAxis getAxis() {
        return OAxis.ofOr(AxisWs.class);
    }

    @Override
    public boolean isEnabled(final HBundle owner) {
        // 访问 NodeNetwork 提取基础配置信息
        final NodeNetwork network = OCacheNode.of(owner).network();
        return network.okSock();
    }
}
