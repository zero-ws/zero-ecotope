package io.zerows.extension.runtime.report.bootstrap;

import io.vertx.core.Vertx;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HAmbient;
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
