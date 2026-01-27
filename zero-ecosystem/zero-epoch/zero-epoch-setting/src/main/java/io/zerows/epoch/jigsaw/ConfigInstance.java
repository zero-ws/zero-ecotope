package io.zerows.epoch.jigsaw;

import io.zerows.epoch.spec.YmVertx;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-09
 */
@Slf4j
public class ConfigInstance extends ConfigNorm {

    private final String name;
    private final ConcurrentMap<String, HConfig> config = new ConcurrentHashMap<>();

    private ConfigInstance(final String name) {
        this.name = name;
    }

    static ConfigInstance of(final YmVertx.Instance instance) {
        final String name = instance.getName();
        final ConfigInstance instanceConfig = new ConfigInstance(name);

        ConfigTool.putOptions(instanceConfig::putConfig,
            instance.getDelivery(),
            instance.getDeployment(),
            instance.getShared()
        );
        instanceConfig.putOptions(instance.getOptions());
        return instanceConfig;
    }

    public String name() {
        return this.name;
    }

    public void putConfig(final EmApp.Native name, final HConfig config) {
        if (ConfigTool.isVertx(name)) {
            throw new IllegalArgumentException("[ ZERO ] 本地配置项" + name + " 非法。");
        }
        this.config.put(name.name(), config);
    }

    public HConfig delivery() {
        return this.config.getOrDefault(EmApp.Native.DELIVERY.name(), null);
    }

    public HConfig deployment() {
        return this.config.getOrDefault(EmApp.Native.DEPLOYMENT.name(), null);
    }

    public HConfig shared() {
        return this.config.getOrDefault(EmApp.Native.SHARED.name(), null);
    }
}
