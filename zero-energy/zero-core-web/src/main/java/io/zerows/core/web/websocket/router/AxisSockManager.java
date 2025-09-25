package io.zerows.core.web.websocket.router;

import io.zerows.core.web.io.management.AxisSockFactory;
import io.zerows.core.web.io.uca.routing.OAxis;
import io.zerows.module.configuration.atom.NodeNetwork;
import io.zerows.module.configuration.store.OCacheNode;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-06-26
 */
public class AxisSockManager implements AxisSockFactory {

    @Override
    public OAxis getAxis() {
        return OAxis.ofOr(AxisWs.class);
    }

    @Override
    public boolean isEnabled(final Bundle owner) {
        // 访问 NodeNetwork 提取基础配置信息
        final NodeNetwork network = OCacheNode.of(owner).network();
        return network.okSock();
    }
}
