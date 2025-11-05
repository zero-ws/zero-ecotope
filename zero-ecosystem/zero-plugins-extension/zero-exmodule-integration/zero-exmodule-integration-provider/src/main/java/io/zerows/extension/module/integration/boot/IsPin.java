package io.zerows.extension.module.integration.boot;

import io.vertx.core.Vertx;
import io.zerows.extension.module.integration.common.IsConfig;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import static io.zerows.extension.module.integration.util.Is.LOG;

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
