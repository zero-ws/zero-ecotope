package io.zerows.extension.mbse.action.plugins;

import io.zerows.epoch.corpus.io.management.AxisDynamicFactory;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
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
    public OAxis getAxis() {
        final Class<OAxis> axisCls = this.options.inComponent();
        final Class<OAxis> axisDefault = (Class<OAxis>) JetPollux.class.asSubclass(OAxis.class);
        return OAxis.ofOr(Objects.isNull(axisCls) ? axisDefault : axisCls);
    }

    @Override
    public boolean isEnabled(final HBundle owner) {
        // 唯一的配置信息，单件模式获取
        final JetPolluxOptions options = JetPolluxOptions.singleton();

        // 检查所有配置是否正确，动态路由是否可发布
        return options.isReady(owner);
    }
}
