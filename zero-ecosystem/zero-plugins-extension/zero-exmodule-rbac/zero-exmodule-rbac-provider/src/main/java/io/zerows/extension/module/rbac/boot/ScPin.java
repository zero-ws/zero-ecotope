package io.zerows.extension.module.rbac.boot;

import io.vertx.core.Vertx;
import io.zerows.extension.module.rbac.metadata.ScConfig;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import static io.zerows.extension.module.rbac.boot.Sc.LOG;

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
