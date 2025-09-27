package io.zerows.extension.commerce.rbac.bootstrap;

import io.vertx.core.Vertx;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.configuration.boot.HRegistry;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/*
 * Init Infusion for `initAsync` static life
 */
public class ScPin implements HRegistry.Mod<Vertx> {


    public static ScConfig getConfig() {
        return ScConfiguration.getConfig();
    }

    /* 新版模块注册器 */
    @Override
    public Boolean configure(final Vertx vertx, final HAmbient ambient) {
        Ke.banner("「Ακριβώς」- Rbac ( Sc )");
        LOG.Init.info(ScPin.class, "ScConfiguration...");
        ScConfiguration.registry(ambient);
        return Boolean.TRUE;
    }
}
