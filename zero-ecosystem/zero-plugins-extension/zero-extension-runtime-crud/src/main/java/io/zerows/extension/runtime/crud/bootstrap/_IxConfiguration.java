package io.zerows.extension.runtime.crud.bootstrap;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.model.extension.HExtension;
import io.zerows.extension.runtime.crud.atom.IxConfig;
import io.zerows.extension.runtime.crud.eon.IxConstant;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.epoch.corpus.metadata.atom.configuration.MDConfiguration;
import io.zerows.specification.access.app.HAmbient;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/*
 * Configuration class initialization
 * plugin/crud/configuration.json
 *
 */
class IxConfiguration {

    /* Module Registry */
    private static final Set<String> MODULE_REG =
        new HashSet<>();
    private static IxConfig CONFIG = null;

    static void registry(final HAmbient ambient) {
        /*
         * Read definition of security configuration from RBAC default folder
         */
        if (null == CONFIG) {
            final MDConfiguration configuration = HExtension.getOrCreate(IxConstant.BUNDLE_SYMBOLIC_NAME);
            final JsonObject configData = configuration.inConfiguration();
            final String module = IxConstant.BUNDLE_SYMBOLIC_NAME;
            LOG.Init.info(IxConfiguration.class, KeMsg.Configuration.DATA_J,
                module, configData.encode());

            ambient.registry(module, configData);

            CONFIG = Ut.deserialize(configData, IxConfig.class);
            LOG.Init.info(IxConfiguration.class, KeMsg.Configuration.DATA_T,
                CONFIG.toString());
        }
    }

    static void addUrs(final String key) {
        final JsonArray patterns = CONFIG.getPatterns();
        patterns.stream()
            .map(item -> (String) item)
            .map(pattern -> MessageFormat.format(pattern, key))
            .forEach(MODULE_REG::add);
    }

    static Set<String> getUris() {
        return MODULE_REG;
    }

    static String getField() {
        return CONFIG.getColumnKeyField();
    }

    static String getLabel() {
        return CONFIG.getColumnLabelField();
    }
}
