package io.zerows.epoch.jigsaw;

import io.r2mo.function.Fn;
import io.zerows.epoch.spec.YmVertx;
import io.zerows.epoch.spec.options.ClusterOptions;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-09
 */
@Slf4j
public class ConfigContainer extends ConfigNorm {

    private final ConcurrentMap<String, HConfig> instances = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, HConfig> config = new ConcurrentHashMap<>();

    private ConfigContainer() {
    }

    static ConfigContainer of(final ClusterOptions options, final YmVertx.Config config) {
        final ConfigContainer container = new ConfigContainer();
        Fn.jvmAt(Objects.nonNull(options), () -> container.putRef(options));


        ConfigTool.putOptions(container::putConfig,
            config.getDelivery(),
            config.getDeployment(),
            config.getShared()
        );


        final List<YmVertx.Instance> instances = config.getInstance();
        instances.forEach(instance -> {
            final HConfig instanceConfig = ConfigInstance.of(instance);
            container.putInstance(instanceConfig);
        });
        return container;
    }

    public void putInstance(final HConfig config) {
        if (!(config instanceof final ConfigInstance instance)) {
            throw new IllegalArgumentException("[ ZERO ] 本地配置项类型不匹配！");
        }
        this.instances.put(instance.name(), instance);
    }

    public HConfig instance(final String name) {
        return this.instances.getOrDefault(name, null);
    }

    public HConfig instance() {
        if (1 == this.instances.size()) {
            return this.instances.values().iterator().next();
        }
        throw new IllegalArgumentException("[ ZERO ] 当前实例数量不唯一，无法直接获取。");
    }

    public Set<String> keyInstance() {
        return this.instances.keySet();
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
