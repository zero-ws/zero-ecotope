package io.zerows.extension.module.ambient.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.assembly.DI;
import io.zerows.extension.module.ambient.service.DocBStub;
import io.zerows.extension.module.ambient.service.DocBuilder;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.extension.skeleton.spi.ExPrerequisite;
import io.zerows.program.Ux;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.support.Ut;

import static io.zerows.extension.module.ambient.boot.At.LOG;

public class AtPin implements HRegistry.Mod<Vertx> {
    private static final DI PLUGIN = DI.create(AtPin.class);

    public static AtConfig getConfig() {
        return AtConfiguration.getConfig();
    }

    public static ExInit getInit() {
        return AtConfiguration.getInit(getConfig().getInitializer());
    }

    public static ExInit getLoader() {
        return AtConfiguration.getInit(getConfig().getLoader());
    }

    public static ExPrerequisite getPrerequisite() {
        return AtConfiguration.getPrerequisite();
    }

    /* 新版模块注册器 */
    @Override
    public Boolean configure(final Vertx vertx, final HAmbient ambient) {
        Ke.banner("「περιβάλλων」- Ambient ( At )");
        LOG.Init.info(AtPin.class, "AtConfiguration...");
        AtConfiguration.registry(ambient);
        return Boolean.TRUE;
    }

    @Override
    public Future<Boolean> initializeAsync(final Vertx container, final HArk ark) {
        final AtConfig config = AtConfiguration.getConfig();
        final boolean disabled = Ut.isNil(config.getFileIntegration());
        if (disabled) {
            LOG.Init.info(this.getClass(), "Document Platform Disabled !!");
            return Ux.futureF();
        }
        // 此处提前调用 initialize 方法，此方法保证无副作用的多次调用即可
        final DocBStub docStub = PLUGIN.createSingleton(DocBuilder.class);
        // Here mapApp function extract `id`
        final HApp app = ark.app();
        final String appId = app.id(); // Ut.valueString(appJ, KName.KEY);
        return docStub.initialize(appId, config.getFileIntegration()).compose(initialized -> {
            LOG.Init.info(this.getClass(), "AppId = {0}, Directory Size = {1}", appId, String.valueOf(initialized.size()));
            return Ux.futureT();
        });
    }
}
