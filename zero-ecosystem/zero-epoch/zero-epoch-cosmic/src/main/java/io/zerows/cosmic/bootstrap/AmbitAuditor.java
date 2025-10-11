package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.extension.PlugAuditor;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.basicore.option.PluginOptions;
import io.zerows.epoch.metadata.MMComponent;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2024-06-27
 */
public class AmbitAuditor implements Ambit {
    private static final Cc<String, PlugAuditor> CC_PLUGIN = Cc.openThread();
    private final PluginOptions option;

    public AmbitAuditor() {
        this.option = PluginOptions.of();
    }

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        // 提取 auditor 部分的 JComponent
        final MMComponent component = this.option.getComponent(YmlCore.extension.AUDITOR);
        if (Objects.isNull(component)) {
            return Future.succeededFuture(envelop);
        }


        final Class<?> auditorCls = component.getComponent();
        Objects.requireNonNull(auditorCls);
        final PlugAuditor auditor = CC_PLUGIN.pick(() -> Ut.instance(auditorCls), auditorCls.getName());
        if (Objects.isNull(auditor)) {
            return Future.succeededFuture(envelop);
        }


        return auditor.bind(component.getConfig()).audit(context, envelop);
    }
}
