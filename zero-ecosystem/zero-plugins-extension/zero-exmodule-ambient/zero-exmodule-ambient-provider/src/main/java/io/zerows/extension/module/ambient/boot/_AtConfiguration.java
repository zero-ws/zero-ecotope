package io.zerows.extension.module.ambient.boot;

import io.vertx.core.json.JsonObject;
import io.zerows.cortex.extension.HExtension;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.skeleton.common.KeMsg;
import io.zerows.specification.app.HAmbient;
import io.zerows.support.Ut;

import java.util.Objects;

import static io.zerows.extension.module.ambient.boot.At.LOG;

@Deprecated
final class AtConfiguration {

    private static AtConfigOld CONFIG = null;

    private AtConfiguration() {
    }

    static void registry(final HAmbient ambient) {
        if (Objects.nonNull(CONFIG)) {
            return;
        }

        // 调用配置注册服务注册新配置，新配置来转换
        final MDConfiguration configuration = HExtension.getOrCreate(AtConstant.BUNDLE_SYMBOLIC_NAME);
        final JsonObject configData = configuration.inConfiguration();
        final String module = AtConstant.BUNDLE_SYMBOLIC_NAME;
        LOG.Init.info(AtConfiguration.class, KeMsg.Configuration.DATA_J,
            module, configData.encode());

        ambient.registry(module, configData);

        CONFIG = Ut.deserialize(configData, AtConfigOld.class);
        LOG.Init.info(AtConfiguration.class, KeMsg.Configuration.DATA_T, CONFIG.toString());
    }

    static AtConfigOld getConfig() {
        return CONFIG;
    }
}
