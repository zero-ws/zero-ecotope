package io.zerows.module.metadata.zdk.service;

import io.zerows.epoch.common.log.LogModule;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import java.util.function.Supplier;

/**
 * 统一提供带有 Dependency 的服务接口，用于分析服务依赖专用处理
 *
 * @author lang : 2024-03-25
 */
public interface ServiceConnector {

    default void serviceDependency(final DependencyManager dm, final Supplier<Component> supplier,
                                   final Supplier<ServiceDependency> dependencySupplier) {
    }

    void serviceRegister(DependencyManager dm, Supplier<Component> supplier);

    /**
     * 日志追踪器，只要实现此接口的方法可以内部调用
     * <pre><code>
     *     this.logger().xxx
     * </code></pre>
     * 通过这种方式可快速提取日志追踪器
     *
     * @return {@link LogModule}
     */
    default OLog logger() {
        return Ut.Log.dependency(this.getClass());
    }
}
