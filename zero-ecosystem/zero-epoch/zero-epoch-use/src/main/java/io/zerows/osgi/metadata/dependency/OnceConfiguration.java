package io.zerows.osgi.metadata.dependency;

import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.platform.enums.EmService;
import io.zerows.osgi.metadata.service.EnergyConfiguration;
import io.zerows.epoch.sdk.osgi.OOnce;
import io.zerows.epoch.sdk.osgi.ServiceContext;
import io.zerows.specification.configuration.HSetting;

import java.util.Objects;

/**
 * @author lang : 2024-07-02
 */
public class OnceConfiguration implements OOnce.LifeCycle<EnergyConfiguration> {
    // 等待服务
    private volatile EnergyConfiguration cachedEnergyConfiguration;

    @Override
    public void bind(final Object reference) {
        if (reference instanceof final EnergyConfiguration energyConfiguration) {
            this.cachedEnergyConfiguration = energyConfiguration;
        }
    }

    @Override
    public boolean isReady() {
        return Objects.nonNull(this.cachedEnergyConfiguration);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R start(final ServiceContext context) {
        // 追加配置相关信息
        final MDConfiguration configuration = context.configuration();
        if (Objects.nonNull(configuration)) {
            this.cachedEnergyConfiguration.addConfiguration(configuration);
        }

        // 追加入口相关信息
        if (EmService.Context.APP == context.type()) {
            final HSetting setting = context.setting();
            Objects.requireNonNull(setting);
            this.cachedEnergyConfiguration.addSetting(context.owner(), setting);
        }
        return (R) configuration;
    }

    @Override
    public void stop(final ServiceContext context) {

    }

    @Override
    public EnergyConfiguration reference() {
        return this.cachedEnergyConfiguration;
    }
}
