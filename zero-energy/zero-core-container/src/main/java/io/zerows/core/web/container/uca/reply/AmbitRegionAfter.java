package io.zerows.core.web.container.uca.reply;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.util.Ut;
import io.zerows.core.web.io.plugins.extension.PlugRegion;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.module.domain.atom.element.JComponent;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.util.Objects;

/**
 * @author lang : 2024-06-27
 */
public class AmbitRegionAfter implements OAmbit {
    private static final Cc<String, PlugRegion> CC_PLUGIN = Cc.openThread();
    private final PluginOption option;

    public AmbitRegionAfter() {
        final Bundle owner = FrameworkUtil.getBundle(this.getClass());
        this.option = PluginOption.of(owner);
    }

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        // 提取 region 部分的 JComponent
        final JComponent component = this.option.getComponent(YmlCore.extension.REGION);
        if (Objects.isNull(component)) {
            return Future.succeededFuture(envelop);
        }


        final Class<?> regionCls = component.getComponent();
        Objects.requireNonNull(regionCls);
        final PlugRegion region = CC_PLUGIN.pick(() -> Ut.instance(regionCls), regionCls.getName());
        if (Objects.isNull(region)) {
            return Future.succeededFuture(envelop);
        }


        return region.bind(component.getConfig()).after(context, envelop);
    }
}
