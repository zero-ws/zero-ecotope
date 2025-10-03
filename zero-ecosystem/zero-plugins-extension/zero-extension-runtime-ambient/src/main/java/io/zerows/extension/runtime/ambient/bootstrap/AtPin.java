package io.zerows.extension.runtime.ambient.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.injection.DiPlugin;
import io.zerows.epoch.corpus.Ux;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.ambient.agent.service.file.DocBStub;
import io.zerows.extension.runtime.ambient.agent.service.file.DocBuilder;
import io.zerows.extension.runtime.skeleton.osgi.spi.extension.Init;
import io.zerows.extension.runtime.skeleton.osgi.spi.extension.Prerequisite;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.configuration.boot.HRegistry;

import static io.zerows.extension.runtime.ambient.util.At.LOG;

public class AtPin implements HRegistry.Mod<Vertx> {
    private static final DiPlugin PLUGIN = DiPlugin.create(AtPin.class);

    public static AtConfig getConfig() {
        return AtConfiguration.getConfig();
    }

    public static Init getInit() {
        return AtConfiguration.getInit(getConfig().getInitializer());
    }

    public static Init getLoader() {
        return AtConfiguration.getInit(getConfig().getLoader());
    }

    public static Prerequisite getPrerequisite() {
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
        // Here mapApp function extract `appId`
        final HApp app = ark.app();
        final String appId = app.appId(); // Ut.valueString(appJ, KName.KEY);
        return docStub.initialize(appId, config.getFileIntegration()).compose(initialized -> {
            LOG.Init.info(this.getClass(), "AppId = {0}, Directory Size = {1}", appId, String.valueOf(initialized.size()));
            return Ux.futureT();
        });
    }
}
