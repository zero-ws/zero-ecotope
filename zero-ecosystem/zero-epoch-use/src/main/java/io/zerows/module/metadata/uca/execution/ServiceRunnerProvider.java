package io.zerows.module.metadata.uca.execution;

import io.vertx.core.Future;
import io.zerows.ams.fn.FnBase;
import io.zerows.module.metadata.atom.service.CallbackParameter;
import io.zerows.module.metadata.zdk.service.ServiceContext;
import io.zerows.module.metadata.zdk.service.ServiceInvocation;
import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author lang : 2024-07-02
 */
class ServiceRunnerProvider extends AbstractServiceRunner {
    ServiceRunnerProvider(final Bundle owner) {
        super(owner);
    }

    @Override
    public void start(final CallbackParameter parameter) {
        this.handleService(parameter, (waitServices, consumer) -> {
            final ServiceContext context = this.energy().getContext(consumer);
            final List<Future<String>> executed = new ArrayList<>();
            waitServices.stream().map(invocation -> Exec.startInvoke(invocation, context))
                .forEach(executed::add);

            FnBase.combineT(executed).onComplete(this::handleExecuted);
        });
    }

    @Override
    public void stop(final CallbackParameter parameter) {
        this.handleService(parameter, (waitServices, consumer) -> {
            final ServiceContext context = this.energy().getContext(consumer);
            final List<Future<String>> executed = new ArrayList<>();
            waitServices.stream().map(invocation -> Exec.stopInvoke(invocation, context))
                .forEach(executed::add);

            FnBase.combineT(executed).onComplete(this::handleExecuted);
        });
    }

    private void handleService(final CallbackParameter parameter,
                               final BiConsumer<Set<ServiceInvocation>, Bundle> consumerFn) {
        final Set<ServiceInvocation> services = parameter.providers();
        // 根据提供者服务计算消费者
        services.forEach(service -> {
            final Set<Bundle> consumers = this.energy().getConsumers(service.id());
            // 单独消费者依次执行
            consumers.forEach(consumer -> {
                final Set<ServiceInvocation> serviceSet = this.waitServices(services, consumer);
                consumerFn.accept(serviceSet, consumer);
            });
        });
    }
}
