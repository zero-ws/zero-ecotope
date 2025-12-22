package io.zerows.extension.module.mbseapi.boot;

import io.vertx.core.Vertx;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.skeleton.metadata.MDSetting;
import io.zerows.specification.configuration.HConfig;

/**
 * 访问 {@link OCacheConfiguration} 提取当前模块的配置信息，根据配置信息构造特殊的元模型对象
 *
 * @author lang : 2025-12-22
 */
class YmMetamodelSetting implements MDSetting<YmMetamodel> {
    @Override
    public YmMetamodel bootstrap(final HConfig config, final Vertx vertx) {
        return null;
    }
}
