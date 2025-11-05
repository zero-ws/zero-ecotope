package io.zerows.extension.module.report.boot;

import io.vertx.core.Vertx;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

/**
 * @author lang : 2024-07-25
 */
public class RpPin implements HRegistry.Mod<Vertx> {

    @Override
    public Boolean configure(final Vertx container, final HAmbient ambient) {
        Ke.banner("「Έντυπο αναφοράς」- Reporting ( Rp )");
        return true;
    }
}
