package io.zerows.extension.runtime.integration.bootstrap;

import io.vertx.core.Vertx;
import io.zerows.extension.runtime.integration.atom.IsConfig;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import static io.zerows.extension.runtime.integration.util.Is.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class IsPin implements HRegistry.Mod<Vertx> {

    public static IsConfig getConfig() {
        return IsConfiguration.getConfig();
    }

    /* 模块注册器 */
    @Override
    public Boolean configure(final Vertx container, final HAmbient ambient) {
        Ke.banner("「Ολοκλήρωση」- Integration ( Is )");
        LOG.Init.info(IsPin.class, "IsConfiguration...");
        IsConfiguration.registry(ambient);
        return true;
    }
}
