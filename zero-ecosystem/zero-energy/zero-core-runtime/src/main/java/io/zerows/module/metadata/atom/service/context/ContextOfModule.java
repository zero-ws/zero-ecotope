package io.zerows.module.metadata.atom.service.context;

import io.zerows.core.util.Ut;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.module.metadata.eon.em.EmService;
import io.zerows.module.metadata.osgi.service.EnergyConfiguration;
import io.zerows.module.metadata.zdk.service.ServiceContext;
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
