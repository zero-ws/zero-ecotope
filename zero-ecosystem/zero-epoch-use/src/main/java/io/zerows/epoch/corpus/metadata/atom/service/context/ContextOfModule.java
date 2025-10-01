package io.zerows.epoch.corpus.metadata.atom.service.context;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.atom.configuration.MDConfiguration;
import io.zerows.epoch.corpus.metadata.eon.em.EmService;
import io.zerows.epoch.corpus.metadata.osgi.service.EnergyConfiguration;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceContext;
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
