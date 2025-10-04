package io.zerows.epoch.metadata.environment;

import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.sdk.osgi.EnergyConfiguration;
import io.zerows.platform.enums.EmService;
import io.zerows.sdk.osgi.ServiceContext;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-07-01
 */
public class ContextOfModule extends ContextOfPlugin {

    private MDConfiguration configuration;

    public ContextOfModule(final Bundle owner) {
        super(owner);
    }

    @Override
    public MDConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public ServiceContext putConfiguration(final MDConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public EmService.Context type() {
        return EmService.Context.MODULE;
    }

    protected EnergyConfiguration service() {
        return Ut.Bnd.service(EnergyConfiguration.class, this.owner());
    }
}
