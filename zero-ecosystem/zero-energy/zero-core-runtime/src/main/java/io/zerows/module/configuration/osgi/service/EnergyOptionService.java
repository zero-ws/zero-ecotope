package io.zerows.module.configuration.osgi.service;

import io.zerows.module.configuration.atom.NodeNetwork;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.configuration.store.OCacheNode;
import io.zerows.module.configuration.store.ORepositoryOption;
import io.zerows.module.metadata.zdk.running.ORepository;
import io.zerows.module.metadata.zdk.service.ServiceContext;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lang : 2024-04-28
 */
public class EnergyOptionService implements EnergyOption {
    @Override
    public NodeNetwork network(final ServiceContext context) {
        final Bundle caller = context.owner();
        final HSetting setting = context.setting();

        ORepository.ofOr(ORepositoryOption.class, caller).whenUpdate(setting);

        return OCacheNode.of(caller).network();
    }

    @Override
    public Set<NodeVertx> vertx(final ServiceContext context) {
        final NodeNetwork network = this.network(context);
        return new HashSet<>(network.vertxOptions().values());
    }
}
