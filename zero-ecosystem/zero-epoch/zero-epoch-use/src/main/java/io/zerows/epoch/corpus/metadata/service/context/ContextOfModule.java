package io.zerows.epoch.corpus.metadata.service.context;

import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.platform.enums.EmService;
import io.zerows.osgi.metadata.service.EnergyConfiguration;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.osgi.ServiceContext;
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
