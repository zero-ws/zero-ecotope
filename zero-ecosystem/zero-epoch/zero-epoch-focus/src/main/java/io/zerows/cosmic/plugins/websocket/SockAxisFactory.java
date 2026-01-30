package io.zerows.cosmic.plugins.websocket;

import io.r2mo.typed.annotation.SPID;
import io.zerows.cortex.AxisFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.jigsaw.NodeNetwork;
import io.zerows.epoch.jigsaw.NodeStore;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-06-26
 */
@SPID(Axis.EID.SOCK)
public class SockAxisFactory implements AxisFactory {

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
