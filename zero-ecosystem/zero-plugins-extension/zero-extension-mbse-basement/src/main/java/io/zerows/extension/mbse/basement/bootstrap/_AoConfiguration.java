package io.zerows.extension.mbse.basement.bootstrap;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.epoch.corpus.extension.HExtension;
import io.zerows.support.Ut;
import io.zerows.extension.mbse.basement.atom.config.AoConfig;
import io.zerows.extension.mbse.basement.eon.AoConstant;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.specification.access.app.HAmbient;

import java.util.Objects;

import static io.zerows.extension.mbse.basement.util.Ao.LOG;

class AoConfiguration {

    private static AoConfig CONFIG = null;

    static void registry(final HAmbient ambient) {
        if (null == CONFIG) {
            final MDConfiguration configuration = HExtension.getOrCreate(AoConstant.BUNDLE_SYMBOLIC_NAME);
            final JsonObject configData = configuration.inConfiguration();
            final String module = AoConstant.BUNDLE_SYMBOLIC_NAME; // Ke.getExtension(KeIpc.Module.ATOM);
            LOG.Init.info(AoConfiguration.class, KeMsg.Configuration.DATA_J,
                module, configData.encode());

            ambient.registry(module, configData);

            CONFIG = Ut.deserialize(configData, AoConfig.class);
            LOG.Init.info(AoConfiguration.class, KeMsg.Configuration.DATA_T,
                CONFIG.toString());
        }
    }

    static AoConfig getConfig() {
        // Fix Issue of Atom Disabled Null Pointer
        return Objects.isNull(CONFIG) ? new AoConfig() : CONFIG;
    }
}
