package io.zerows.epoch.sdk.osgi;

import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.epoch.corpus.metadata.service.CallbackParameter;
import io.zerows.epoch.corpus.metadata.service.context.ContextOfApp;
import io.zerows.epoch.corpus.metadata.service.context.ContextOfPlugin;
import io.zerows.osgi.metadata.dependency.CallbackOfService;
import io.zerows.osgi.metadata.service.EnergyConfiguration;
import io.zerows.osgi.metadata.service.EnergyDeployment;
import io.zerows.osgi.metadata.service.EnergyService;
import io.zerows.epoch.program.Ut;
import io.zerows.specification.configuration.HSetting;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 功能
 * <pre><code>
 *     继承功能：异常依赖服务
 *     * 服务管理器：
 *     - 等待 ServiceContext.OK 信号量，信号量完成后证明以下配置已经完成
 *       {@link HSetting} 已加载完成
 *       {@link MDConfiguration} 也已经加载完成
 *     理论上讲，所有的非 extension 部分的模块都会构造两种 Context
 *     - {@link ContextOfPlugin} 普通专用插件信息，后期可能重新修订插件基础配置
 *     - {@link ContextOfApp}    入口专用插件信息
 * </code></pre>
 *
 * @author lang : 2024-07-01
 */
public abstract class AbstractConnectorService extends AbstractConnectorBase {
    protected AbstractConnectorService(final Bundle owner) {
        super(owner);
    }

    @Override
    public void serviceDependency(final DependencyManager dm, final Supplier<Component> supplier,
                                  final Supplier<ServiceDependency> dependency) {
        super.serviceDependency(dm, supplier, dependency);


        // 构造 Callback，如果内置 MDConfiguration 则直接在 withContext 中构造
        final Bundle owner = this.bundle;
        final ServiceContext context = this.withContext(owner);
        Objects.requireNonNull(context);


        // 服务参数
        final CallbackParameter parameter = new CallbackParameter(context, dm)
            .consumers(this.withConsumers(owner))
            .providers(this.withProviders(owner));

        // 构造 Callback 组件
        final CallbackOfService callbackComponent = this.withComponent(parameter);
        final Component callback = supplier.get().setImplementation(callbackComponent);
        dm.add(Ut.Bnd.addDependency(callback, dependency, this.buildDependency()));
    }

    protected Class<?>[] buildDependency() {
        return new Class<?>[]{
            // 配置管理
            EnergyConfiguration.class,

            // 服务管理
            EnergyService.class, ServiceContext.OK.class,

            // 组件发布流程（必须）
            EnergyDeployment.class
        };
    }

    // ----------------- 新版直接处理生命周期 ----------------------

    /*
     * 需要重写哪个方法就选择性重写，主要是为了简化代码，核心重写方法场景如
     * - withContext(Bundle)        / 服务上下文 / 默认 ServiceContext.ofPlugin
     * - withProviders(Bundle)      / 服务提供者
     * - withConsumers(Bundle)      / 服务消费者
     * - withComponent(Bundle)     /  参数最终处理并构造 CallbackOfService
     */
    // 1. 默认构造服务上下文的专用函数
    protected ServiceContext withContext(final Bundle owner) {
        return ServiceContext.ofPlugin(owner);
    }

    // 2. 默认构造 Service 专用函数：Provider / Consumer
    protected ServiceInvocation[] withProviders(final Bundle owner) {
        return new ServiceInvocation[]{};
    }

    protected String[] withConsumers(final Bundle owner) {
        return new String[]{};
    }

    // 3. 后期处理执行（额外步骤）
    protected CallbackOfService withComponent(final CallbackParameter parameter) {
        return new CallbackOfService(parameter);
    }
}
