package io.zerows.epoch.configuration;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.spec.InPreArgs;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.specification.app.HApp;
import io.zerows.spi.HPI;

public interface ConfigProvider {

    Cc<String, ConfigProvider> CC_PROVIDER = Cc.openThread();

    static ConfigProvider of(final String selected) {
        final String name = name(selected);
        return CC_PROVIDER.pick(() -> HPI.findOne(ConfigProvider.class, name), name);
    }

    static String name(final String selected) {
        return "ConfigServer/" + selected;
    }

    ConfigFs<YmConfiguration> configure(InPreArgs config, HApp app);
}
