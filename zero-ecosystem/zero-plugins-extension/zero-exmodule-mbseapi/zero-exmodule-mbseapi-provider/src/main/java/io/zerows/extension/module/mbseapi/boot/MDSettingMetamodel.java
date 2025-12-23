package io.zerows.extension.module.mbseapi.boot;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.skeleton.metadata.MDSetting;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

/**
 * 访问 {@link OCacheConfiguration} 提取当前模块的配置信息，根据配置信息构造特殊的元模型对象
 *
 * @author lang : 2025-12-22
 */
class MDSettingMetamodel implements MDSetting<MDCMetamodel> {
    @Override
    public MDCMetamodel bootstrap(final HConfig config, final Vertx vertx) {
        final JsonObject options = config.options();
        if (Ut.isNil(options)) {
            return null;
        }
        return Ut.deserialize(options, MDCMetamodel.class);
    }
}
