package io.zerows.extension.mbse.action.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.extension.mbse.action.atom.JtConfig;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.mbse.action.util.Jt.LOG;

/*
 * Configuration of zero here
 * This extension configuration is different from other extension
 * The json config must be set in `vertx-jet.yml` or other tp extension
 */
public class JtPin implements HRegistry.Mod<Vertx> {

    public static JtConfig getConfig() {
        return JtConfiguration.getConfig();
    }

    public static ConcurrentMap<String, ServiceEnvironment> serviceEnvironment() {
        return JtConfiguration.serviceEnvironment();
    }

    @Override
    public Future<Boolean> configureAsync(final Vertx container, final HAmbient ambient) {
        Ke.banner("「Πίδακας δρομολογητή」- ( Api )");
        LOG.Init.info(this.getClass(), "JtConfiguration...");
        JtConfiguration.registry(ambient);
        LOG.Init.info(this.getClass(), "HAmbient Environment Start...");
        return JtConfiguration.init(container, ambient);
    }
}
