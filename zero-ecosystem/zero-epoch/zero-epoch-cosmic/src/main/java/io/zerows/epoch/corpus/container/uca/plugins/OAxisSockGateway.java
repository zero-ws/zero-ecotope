package io.zerows.epoch.corpus.container.uca.plugins;

import io.zerows.epoch.corpus.io.management.AxisSockFactory;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.spi.HPI;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2024-06-27
 */
public class OAxisSockGateway implements OAxisGateway {
    private static final AtomicBoolean IS_LOG = new AtomicBoolean(Boolean.TRUE);

    @Override
    public OAxis getAxis(final Bundle owner) {
        final AxisSockFactory factory = HPI.findOverwrite(AxisSockFactory.class);
        if (Objects.isNull(factory)) {
            // 没有部署，无法找到工厂类
            if (IS_LOG.getAndSet(Boolean.FALSE)) {
                this.logger().info("The AxisSockFactory is null and websocket feature will be disabled.");
            }
            return null;
        }

        if (!factory.isEnabled(owner)) {
            // 没有启用
            if (IS_LOG.getAndSet(Boolean.FALSE)) {
                this.logger().info("The websocket feature is disabled by configuration, please contact administrator.");
            }
            return null;
        }
        return factory.getAxis();
    }
}
