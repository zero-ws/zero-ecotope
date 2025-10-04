package io.zerows.sdk.osgi;

import io.vertx.core.AsyncResult;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-07-02
 */
public abstract class ServiceRunnerBase implements ServiceRunner {
    private final Bundle owner;
    private volatile EnergyService energyService;

    protected ServiceRunnerBase(final Bundle owner) {
        this.owner = owner;
    }

    protected Bundle owner() {
        return this.owner;
    }

    protected EnergyService energy() {
        if (Objects.isNull(this.energyService)) {
            this.energyService = Ut.Bnd.service(EnergyService.class, this.owner);
        }
        return this.energyService;
    }

    // --------------- 消费和生产模式
    /*
     * 根据消费者 Bundle 和服务 ID 提取服务信息
     */
    protected Set<ServiceInvocation> waitServices(final Set<ServiceInvocation> serviceSet, final Bundle consumer) {
        final Set<ServiceInvocation> servicePending = new HashSet<>();
        final Set<ServiceInvocation> serviceInput = this.getServices(serviceSet, consumer);

        // 执行记录
        final ServiceExecuted executed = ServiceExecuted.of(consumer);
        final Set<String> executedIds = executed.getExecuted(consumer.getBundleId());

        final ConcurrentMap<String, ServiceInvocation> serviceMap = this.energy().serviceMap();
        serviceInput.stream()
            .filter(service -> serviceMap.containsKey(service.id()))        // 合法服务
            .filter(service -> !executedIds.contains(service.id()))         // 未被执行的服务
            .forEach(servicePending::add);
        return servicePending;
    }

    protected Set<ServiceInvocation> waitServicesById(final Set<String> serviceIds, final Bundle consumer) {
        final Set<String> serviceInput = this.getServices(serviceIds, consumer);

        final ConcurrentMap<String, ServiceInvocation> serviceMap = this.energy().serviceMap();
        final Set<ServiceInvocation> invocations = serviceInput.stream()
            .map(serviceMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        return this.waitServices(invocations, consumer);
    }

    private <T> Set<T> getServices(final Set<T> serviceSet, final Bundle consumer) {
        // 输入为 null
        if (serviceSet.isEmpty()) {
            return Set.of();
        }
        // 服务管理器 null
        final EnergyService energy = this.energy();
        if (Objects.isNull(energy)) {
            this.logger().warn("(1).Energy Service is null, owner = {}", consumer.getSymbolicName());
            return Set.of();
        }
        // 服务上下文提取
        final ServiceContext context = energy.getContext(consumer);
        if (Objects.isNull(context)) {
            this.logger().warn("(2).ServiceContext is null in your environment, owner = {}", consumer.getSymbolicName());
            return Set.of();
        }
        return serviceSet;
    }

    protected void handleExecuted(final AsyncResult<List<String>> executed) {
        if (executed.succeeded()) {
            this.logger().info("Service executed successfully.");
        } else {
            this.logger().error("Service executed failed. ex = {}", executed.cause().getMessage());
        }
    }
}
