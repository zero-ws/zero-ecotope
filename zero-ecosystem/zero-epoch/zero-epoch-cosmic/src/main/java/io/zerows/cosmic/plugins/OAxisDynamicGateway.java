package io.zerows.cosmic.plugins;

import io.zerows.cortex.AxisDynamicFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

import java.util.Objects;

/**
 * @author lang : 2024-06-27
 */
public class OAxisDynamicGateway implements OAxisGateway {
    @Override
    public Axis getAxis(final HBundle owner) {
        final AxisDynamicFactory factory = HPI.findOverwrite(AxisDynamicFactory.class);
        if (Objects.isNull(factory)) {
            // 没有部署，无法找到工厂类
            return null;
        }

        if (!factory.isEnabled(owner)) {
            // 没有启用
            return null;
        }
        return factory.getAxis();
    }
}
