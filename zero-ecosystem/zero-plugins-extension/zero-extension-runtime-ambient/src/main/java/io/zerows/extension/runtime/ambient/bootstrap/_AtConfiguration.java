package io.zerows.extension.runtime.ambient.bootstrap;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.cortex.extension.HExtension;
import io.zerows.extension.runtime.ambient.eon.AtConstant;
import io.zerows.extension.runtime.ambient.exception._80302Exception500InitSpecification;
import io.zerows.extension.runtime.ambient.exception._80303Exception500PrerequisiteSpec;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.extension.runtime.skeleton.osgi.spi.extension.Init;
import io.zerows.extension.runtime.skeleton.osgi.spi.extension.Prerequisite;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.support.Ut;

import java.util.Objects;

import static io.zerows.extension.runtime.ambient.util.At.LOG;

final class AtConfiguration {

    private static AtConfig CONFIG = null;

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

        CONFIG = Ut.deserialize(configData, AtConfig.class);
        LOG.Init.info(AtConfiguration.class, KeMsg.Configuration.DATA_T, CONFIG.toString());
    }

    static AtConfig getConfig() {
        return CONFIG;
    }

    static Init getInit(final Class<?> initClass) {
        if (Objects.isNull(initClass)) {
            return null;
        } else {
            Fn.jvmKo(!Ut.isImplement(initClass, Init.class), _80302Exception500InitSpecification.class, initClass.getName());
            return Init.generate(initClass);
        }
    }

    static Prerequisite getPrerequisite() {
        final Class<?> prerequisite = CONFIG.getPrerequisite();
        if (Objects.isNull(prerequisite)) {
            return null;
        } else {
            Fn.jvmKo(!Ut.isImplement(prerequisite, Prerequisite.class), _80303Exception500PrerequisiteSpec.class, prerequisite.getName());
            return Prerequisite.generate(prerequisite);
        }
    }
}
