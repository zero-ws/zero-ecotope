package io.zerows.epoch.configuration;

import io.r2mo.typed.annotation.SPID;
import io.zerows.epoch.spec.InPreArgs;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.specification.app.HApp;

@SPID("ConfigServer/nacos")     // 必须的ID配置
public class ConfigProviderNacos implements ConfigProvider {
    @Override
    public YmConfiguration configure(final InPreArgs config, final HApp app) {
        return null;
    }
}
