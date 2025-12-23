package io.zerows.extension.module.mbseapi.plugins;

import io.zerows.cortex.AxisDynamicFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.extension.module.mbseapi.boot.MDMBSEManager;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-06-26
 */
public class JetAxisManager implements AxisDynamicFactory {

    private final MDMBSEManager manager;

    public JetAxisManager() {
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
