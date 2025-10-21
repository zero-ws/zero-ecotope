package io.zerows.extension.mbse.ui.bootstrap;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

public class UiPin implements HRegistry.Mod<Vertx> {

    public static JsonArray getOp() {
        return UiConfiguration.getOp();
    }

    public static JsonArray getColumn(final String identifier) {
        return UiConfiguration.getColumn(identifier);
    }

    public static JsonArray attributes(final String identifier) {
        return UiConfiguration.attributes(identifier);
    }

    public static String keyControl() {
        return UiConfiguration.keyControl();
    }

    public static String keyOps() {
        return UiConfiguration.keyOps();
    }

    public static int keyExpired() {
        return UiConfiguration.getConfig().getCacheExpired();
    }

    @Override
    public Boolean configure(final Vertx vertx, final HAmbient ambient) {
        Ke.banner("「Διασύνδεση χρήστη」- ( Ui )");
        LOG.Init.info(UiPin.class, "UiConfiguration...");
        UiConfiguration.registry(ambient);
        return Boolean.TRUE;
    }
}
