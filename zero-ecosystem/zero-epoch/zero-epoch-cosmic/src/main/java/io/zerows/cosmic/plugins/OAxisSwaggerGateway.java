package io.zerows.cosmic.plugins;

import io.zerows.cortex.AxisSwaggerFactory;
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
public class OAxisSwaggerGateway implements OAxisGateway {
    private static final AtomicBoolean IS_LOG = new AtomicBoolean(Boolean.TRUE);

    @Override
    public Axis getAxis(final HBundle owner) {
        final AxisSwaggerFactory factory = HPI.findOverwrite(AxisSwaggerFactory.class);
        if (Objects.isNull(factory)) {
            // 没有部署，无法找到工厂类
            if (IS_LOG.getAndSet(Boolean.FALSE)) {
                log.info("[ ZERO ] ( Swagger ) ⚠️ SPI 组件 AxisSwaggerFactory 为 null，Swagger 功能禁用！");
            }
            return null;
        }
        return factory.getAxis();
    }


}
