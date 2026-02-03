package io.zerows.plugins.swagger;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Vertx;
import io.zerows.cortex.AxisFactory;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

@SPID(Axis.EID.OPEN)
public class SwaggerAxisFactory implements AxisFactory {

    @Override
    public Axis getAxis() {
        return SwaggerAxis.of();
    }

    @Override
    public boolean isEnabled(final HBundle owner) {
        final Vertx vertxRef = StoreVertx.of(owner).vertx();
        final SwaggerConfig config = SwaggerActor.registryOf(vertxRef);
        if (Objects.isNull(config)) {
            return false;
        }
        return config.isEnabled();
    }
}
