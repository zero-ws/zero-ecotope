package io.zerows.extension.module.mbseapi.plugins;

import io.zerows.cortex.AxisDynamicFactory;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-06-26
 */
public class JetAxisManager implements AxisDynamicFactory {

    private final JetPolluxOptions options;

    public JetAxisManager() {
        this.options = JetPolluxOptions.singleton();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Axis getAxis() {
        final Class<Axis> axisCls = this.options.inComponent();
        final Class<Axis> axisDefault = (Class<Axis>) JetPollux.class.asSubclass(Axis.class);
        return Axis.ofOr(Objects.isNull(axisCls) ? axisDefault : axisCls);
    }

    @Override
    public boolean isEnabled(final HBundle owner) {
        // 唯一的配置信息，单件模式获取
        final JetPolluxOptions options = JetPolluxOptions.singleton();

        // 检查所有配置是否正确，动态路由是否可发布
        return options.isReady(owner);
    }
}
