package io.zerows.extension.runtime.ambient.bootstrap;

import io.vertx.core.json.JsonObject;
import io.zerows.core.fn.FnZero;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.extension.HExtension;
import io.zerows.extension.runtime.ambient.eon.AtConstant;
import io.zerows.extension.runtime.ambient.exception._500InitSpecificationException;
import io.zerows.extension.runtime.ambient.exception._500PrerequisiteSpecException;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.extension.runtime.skeleton.osgi.spi.extension.Init;
import io.zerows.extension.runtime.skeleton.osgi.spi.extension.Prerequisite;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.specification.access.app.HAmbient;

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
            FnZero.outWeb(!Ut.isImplement(initClass, Init.class), _500InitSpecificationException.class,
                AtPin.class, initClass.getName());
            return Init.generate(initClass);
        }
    }

    static Prerequisite getPrerequisite() {
        final Class<?> prerequisite = CONFIG.getPrerequisite();
        if (Objects.isNull(prerequisite)) {
            return null;
        } else {
            FnZero.outWeb(!Ut.isImplement(prerequisite, Prerequisite.class), _500PrerequisiteSpecException.class,
                AtPin.class, prerequisite.getName());
            return Prerequisite.generate(prerequisite);
        }
    }
}
