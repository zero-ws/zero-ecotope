package io.zerows.epoch.corpus.container.osgi.service.provider;

import io.vertx.core.Future;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.corpus.container.osgi.service.EnergyVertx;
import io.zerows.osgi.configuration.service.EnergyOption;
import io.zerows.osgi.metadata.service.EnergyDeployment;
import io.zerows.sdk.osgi.AbstractServiceInvocation;
import io.zerows.sdk.osgi.ServiceContext;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-07-02
 */
public class InvocationContainer extends AbstractServiceInvocation {
    public InvocationContainer(final Bundle provider) {
        super(provider);
    }

    @Override
    public String id() {
        return ISV.INVOCATION_CONTAINER;
    }

    @Override
    public Future<Boolean> start(final ServiceContext context) {
        final EnergyOption energyOption = this.service(EnergyOption.class);
        Objects.requireNonNull(energyOption);
        final NodeNetwork network = energyOption.network(context);
        // 启动 Vertx 实例
        final EnergyVertx energyVertx = this.service(EnergyVertx.class);
        // 提取 Deployment 服务
        final EnergyDeployment energyDeployment = this.service(EnergyDeployment.class);

        return energyVertx.startAsync(this.provider(), network).compose(storeVertx -> {
            // 双发布流程
            energyDeployment
                // Agent / Worker
                .runDeploy(context.owner(), storeVertx)
                // Infusion / Codex
                .runDeployPlugins(context.owner(), storeVertx);
            return Future.succeededFuture(Boolean.TRUE);
        });
    }
}
