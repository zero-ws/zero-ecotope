package io.zerows.extension.mbse.modulat.bootstrap;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.extension.HExtension;
import io.zerows.extension.mbse.modulat.eon.BkConstant;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.module.metadata.store.OZeroStore;

import java.util.HashSet;
import java.util.Set;

import static io.zerows.extension.mbse.modulat.util.Bk.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class BkConfiguration {
    private static final Set<String> BUILD_IN = new HashSet<>();

    private BkConfiguration() {
    }

    static void init() {
        if (OZeroStore.is(YmlCore.module.__KEY)) {
            final MDConfiguration configuration = HExtension.getOrCreate(BkConstant.BUNDLE_SYMBOLIC_NAME);
            final JsonObject configData = configuration.inConfiguration();
            final JsonArray buildInArr = Ut.valueJArray(configData, "builtIn");
            BUILD_IN.addAll(Ut.toSet(buildInArr));
            LOG.Init.info(BkConfiguration.class, "The Modulat Engine will be initialized!! `{0}`",
                configData.encode());
        }
    }

    static Set<String> builtIn() {
        return BUILD_IN;
    }
}
