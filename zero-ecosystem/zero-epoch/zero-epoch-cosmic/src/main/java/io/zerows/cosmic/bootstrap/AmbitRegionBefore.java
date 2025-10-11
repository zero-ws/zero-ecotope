package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.extension.PlugRegion;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.basicore.option.PluginOptions;
import io.zerows.epoch.metadata.MMComponent;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2024-06-27
 */
public class AmbitRegionBefore implements Ambit {
    private static final Cc<String, PlugRegion> CC_PLUGIN = Cc.openThread();
    private final PluginOptions option;

    public AmbitRegionBefore() {
        this.option = PluginOptions.of();
    }

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        // 提取 region 部分的 JComponent
        final MMComponent component = this.option.getComponent(YmlCore.extension.REGION);
        if (Objects.isNull(component)) {
            return Future.succeededFuture(envelop);
        }


        final Class<?> regionCls = component.getComponent();
        Objects.requireNonNull(regionCls);
        final PlugRegion region = CC_PLUGIN.pick(() -> Ut.instance(regionCls), regionCls.getName());
        if (Objects.isNull(region)) {
            return Future.succeededFuture(envelop);
        }


        return region.bind(component.getConfig()).before(context, envelop);
    }
}
