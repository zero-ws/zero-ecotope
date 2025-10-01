package io.zerows.epoch.sdk.metadata.service;

import io.vertx.core.Future;
import org.osgi.framework.Bundle;

/**
 * 服务调用者，此服务调用者实现由服务提供者 Provider 定义，而 id 会作为内部调用对象被 Consumer 消费，实现完整上线的服务调用流程，执行过程
 * 属于异步调用，OSGI 早期是同步流程，从服务调用开始直接走异步。
 *
 * @author lang : 2024-07-01
 */
public interface ServiceInvocation {
    /**
     * 服务提供者引用
     *
     * @return Bundle
     */
    Bundle provider();

    /**
     * 服务 ID
     *
     * @return String
     */
    String id();

    Future<Boolean> start(ServiceContext context);

    Future<Boolean> stop(ServiceContext context);

    interface ISV {
        String INVOCATION_ASSEMBLY = "INVOCATION.ASSEMBLY.SERVICE";         // 动态类扫描服务，用来扫描合法的组件元数据
        String INVOCATION_CONTAINER = "INVOCATION.CONTAINER.SERVICE";       // 容器类服务，专用于处理容器启动
        String INVOCATION_APPLICATION = "INVOCATION.APPLICATION.SERVICE";   // 应用类服务
    }
}
