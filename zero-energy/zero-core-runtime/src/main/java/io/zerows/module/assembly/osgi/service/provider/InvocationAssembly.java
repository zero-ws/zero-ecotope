package io.zerows.module.assembly.osgi.service.provider;

import io.vertx.core.Future;
import io.zerows.module.assembly.osgi.service.EnergyClass;
import io.zerows.module.metadata.osgi.service.EnergyDeployment;
import io.zerows.module.metadata.zdk.dependency.AbstractServiceInvocation;
import io.zerows.module.metadata.zdk.service.ServiceContext;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-07-01
 */
public class InvocationAssembly extends AbstractServiceInvocation {

    public InvocationAssembly(final Bundle provider) {
        super(provider);
    }

    @Override
    public String id() {
        return ISV.INVOCATION_ASSEMBLY;
    }

    @Override
    public Future<Boolean> start(final ServiceContext context) {
        final EnergyClass energy = this.service(EnergyClass.class);
        Objects.requireNonNull(energy);


        // 本身的扫描服务
        final Bundle consumer = context.owner();
        energy.install(consumer);


        // 发布服务
        final EnergyDeployment deploy = this.service(EnergyDeployment.class);
        if (Objects.nonNull(deploy)) {
            deploy.runDeploy(consumer);
        }
        return Future.succeededFuture(Boolean.TRUE);
    }

    @Override
    public Future<Boolean> stop(final ServiceContext context) {
        final EnergyClass energy = this.service(EnergyClass.class);
        Objects.requireNonNull(energy);


        // 本身的扫描服务
        final Bundle consumer = context.owner();
        energy.uninstall(consumer);


        // 撤销服务
        final EnergyDeployment deploy = this.service(EnergyDeployment.class);
        if (Objects.nonNull(deploy)) {
            deploy.runUndeploy(consumer);
        }
        return Future.succeededFuture(Boolean.TRUE);
    }
}
