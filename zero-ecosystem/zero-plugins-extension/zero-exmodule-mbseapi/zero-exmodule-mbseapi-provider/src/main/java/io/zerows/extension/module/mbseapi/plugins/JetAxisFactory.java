package io.zerows.extension.module.mbseapi.plugins;

import io.r2mo.typed.annotation.SPID;
import io.zerows.cortex.AxisFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.extension.module.mbseapi.boot.MDMBSEManager;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-06-26
 */
@SPID(Axis.EID.MBSE)
public class JetAxisFactory implements AxisFactory {

    private final MDMBSEManager manager;

    public JetAxisFactory() {
        this.manager = MDMBSEManager.of();
    }

    @Override
    public Axis getAxis() {
        return Objects.requireNonNull(this.manager).getAxis();
    }

    @Override
    public boolean isEnabled(final HBundle owner) {
        // 唯一的配置信息，单件模式获取
        return Objects.requireNonNull(this.manager).isEnabled(owner);
    }
}
