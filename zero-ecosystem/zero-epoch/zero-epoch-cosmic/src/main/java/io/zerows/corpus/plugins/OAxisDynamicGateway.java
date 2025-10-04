package io.zerows.corpus.plugins;

import io.zerows.epoch.corpus.io.management.AxisDynamicFactory;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

import java.util.Objects;

/**
 * @author lang : 2024-06-27
 */
public class OAxisDynamicGateway implements OAxisGateway {
    @Override
    public OAxis getAxis(final HBundle owner) {
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
