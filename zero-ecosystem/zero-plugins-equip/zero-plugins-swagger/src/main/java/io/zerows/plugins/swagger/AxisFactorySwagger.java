package io.zerows.plugins.swagger;

import io.r2mo.typed.annotation.SPID;
import io.zerows.cortex.AxisFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;

@SPID(Axis.EID.OPEN)
public class AxisFactorySwagger implements AxisFactory {

    @Override
    public Axis getAxis() {
        return null;
    }

    @Override
    public boolean isEnabled(final HBundle owner) {
        return false;
    }

}
