package io.zerows.extension.module.mbsecore.boot;

import io.vertx.core.Vertx;
import io.zerows.extension.module.mbsecore.metadata.config.AoConfig;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import static io.zerows.extension.module.mbsecore.boot.Ao.LOG;

public class AoPin implements HRegistry.Mod<Vertx> {
    public static AoConfig getConfig() {
        return AoConfiguration.getConfig();
    }

    /** 模块注册器 */
    @Override
    public Boolean configure(final Vertx container, final HAmbient ambient) {
        Ke.banner("「διαμορφωτής」- Atom ( Ao )");
        LOG.Init.info(AoPin.class, "AoConfiguration...");
        AoConfiguration.registry(ambient);
        return true;
    }
}
