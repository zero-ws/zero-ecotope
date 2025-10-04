package io.zerows.extension.mbse.basement.bootstrap;

import io.vertx.core.Vertx;
import io.zerows.extension.mbse.basement.atom.config.AoConfig;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import static io.zerows.extension.mbse.basement.util.Ao.LOG;

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
