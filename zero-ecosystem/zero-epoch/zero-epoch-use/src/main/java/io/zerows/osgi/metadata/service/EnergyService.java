package io.zerows.osgi.metadata.service;

import io.zerows.component.log.OLog;
import io.zerows.support.Ut;
import io.zerows.sdk.osgi.ServiceContext;
import io.zerows.sdk.osgi.ServiceInvocation;
import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务管理器主接口
 *
 * @author lang : 2024-07-01
 */
public interface EnergyService {
    // ----------- 服务提取
    Set<ServiceInvocation> serviceSet();

    default ConcurrentMap<String, ServiceInvocation> serviceMap() {
        return Ut.elementMap(new ArrayList<>(this.serviceSet()), ServiceInvocation::id);
    }

    // ---- Role = ServiceContext
    void addContext(ServiceContext context);

    ServiceContext getContext(Bundle owner);

    void removeContext(ServiceContext context);

    // ---- Role = ServiceProvider
    void addProviderService(ServiceInvocation service);

    void removeProviderService(ServiceInvocation service);

    void removeProvider(Bundle provider);

    Set<ServiceInvocation> getProviderService(Bundle provider);


    // ---- Role = ServiceConsumer
    void addConsumerService(Bundle consumer, String serviceId);

    void removeConsumerService(Bundle consumer, String serviceId);

    void removeConsumer(Bundle consumer);

    Set<ServiceInvocation> getConsumerService(Bundle consumer);

    Set<Bundle> getConsumers(String serviceId);

    // ---- 日志记录器
    default OLog logger() {
        return Ut.Log.service(this.getClass());
    }
}
