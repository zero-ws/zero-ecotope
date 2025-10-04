package io.zerows.extension.commerce.rbac.bootstrap;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.corpus.extension.HExtension;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.eon.ScConstant;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.support.Ut;

import java.util.Objects;

/*
 * Configuration class initialization
 * plugin/rbac/configuration.json
 *
 */
class ScConfiguration {
    private static ScConfig CONFIG = null;

    static void registry(final HAmbient ambient) {
        /*
         * Read definition of security configuration from RBAC default folder
         */
        if (null == CONFIG) {
            CONFIG = getConfig();
        }

        final MDConfiguration configuration = HExtension.getOrCreate(ScConstant.BUNDLE_SYMBOLIC_NAME);
        final JsonObject configData = configuration.inConfiguration();
        final String module = ScConstant.BUNDLE_SYMBOLIC_NAME;
        ambient.registry(module, configData);
    }

    static ScConfig getConfig() {
        if (Objects.isNull(CONFIG)) {
            final MDConfiguration configuration = HExtension.getOrCreate(ScConstant.BUNDLE_SYMBOLIC_NAME);
            final JsonObject configData = configuration.inConfiguration();
            Ut.Log.configure(ScConfiguration.class).info(KeMsg.Configuration.DATA_J,
                ScConstant.BUNDLE_SYMBOLIC_NAME, configData.encode());
            CONFIG = Ut.deserialize(configData, ScConfig.class);
            Ut.Log.configure(ScConfiguration.class).info(KeMsg.Configuration.DATA_T,
                CONFIG.toString());
        }
        return CONFIG;
    }
}
