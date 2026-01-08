package io.zerows.cosmic.plugins;

import io.zerows.cortex.AxisSockFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2024-06-27
 */
@Slf4j
public class OAxisSockGateway implements OAxisGateway {
    private static final AtomicBoolean IS_LOG = new AtomicBoolean(Boolean.TRUE);

    @Override
    public Axis getAxis(final HBundle owner) {
        final AxisSockFactory factory = HPI.findOverwrite(AxisSockFactory.class);
        if (Objects.isNull(factory)) {
            // 没有部署，无法找到工厂类
            if (IS_LOG.getAndSet(Boolean.FALSE)) {
                log.debug("[ ZERO ] ( WebSocket ) ⚠️ SPI 组件 AxisSockFactory 为 null，WebSocket 功能禁用！");
            }
            return null;
        }

        if (!factory.isEnabled(owner)) {
            // 没有启用
            if (IS_LOG.getAndSet(Boolean.FALSE)) {
                log.debug("[ ZERO ] ( WebSocket ) ⚠️ 功能被禁用，若想要打开，请检查配置 / 联系管理员！");
            }
            return null;
        }
        return factory.getAxis();
    }
}
