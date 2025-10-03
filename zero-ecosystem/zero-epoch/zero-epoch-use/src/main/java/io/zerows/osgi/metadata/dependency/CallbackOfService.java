package io.zerows.osgi.metadata.dependency;

import io.zerows.component.execution.ServiceRunner;
import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.epoch.corpus.metadata.service.CallbackParameter;
import io.zerows.osgi.metadata.service.EnergyConfiguration;
import io.zerows.osgi.metadata.service.EnergyDeployment;
import io.zerows.osgi.metadata.service.EnergyFailure;
import io.zerows.osgi.metadata.service.EnergyService;
import io.zerows.epoch.sdk.osgi.OCallback;
import io.zerows.epoch.sdk.osgi.OOnce;
import io.zerows.epoch.sdk.osgi.ServiceContext;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.configuration.HSetting;
import org.apache.felix.dm.DependencyManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 服务专用回调，用来带有服务管理器的回调模型
 * <pre><code>
 *     1. 配置管理追加 {@link EnergyConfiguration}
 *     2. 服务管理追加 {@link EnergyService}
 *     3. 发布管理追加 {@link EnergyDeployment}
 *     4. 一定要等待 ServiceContext.OK 的信号量才可以继续
 * </code></pre>
 *
 * @author lang : 2024-07-01
 */
public class CallbackOfService implements OCallback.Standard {

    private final CallbackParameter parameter;
    private final OOnce<EnergyService> onceService;

    private final List<OOnce<?>> okRefs = new ArrayList<>();

    /**
     * 回调构造，直接使用回调参数构造回调服务，顶层的抽象回调服务的父类就是 CallbackOfService，所以子类构造时可以直接根据需求追加 okRefs
     * <pre><code>
     *      okRefs 是带顺序的条件链，此处有几点需说明：
     *      1）{@link EnergyService} 带有两种形态，为服务管理专用。
     *         - 第一种形态入口专用，不带 {@link ServiceContext.OK} 信号量
     *         - 第二种形态组件专用，同时带有 {@link EnergyService} 和 {@link ServiceContext.OK} 信号量
     *      2）{@link EnergyConfiguration} 配置服务。
     *      3）{@link EnergyDeployment} 发布服务。
     *      以上三种服务是必须的，且位于其他服务之前，其他服务可以根据业务需求追加。
     * </code></pre>
     * 关于服务的特殊说明
     * <pre><code>
     *     1）{@link EnergyDeployment} 发布服务的实现类位于上层，在容器插件中，Container 会提供发布服务的相关，实现并将服务注册到
     *        OSGI 容器中，OSGI 借用 {@link DependencyManager} 来实现服务的注册/消费流程。
     *     2）{@link HApp} 和 {@link HArk} 两种服务为应用环境，每个应用会对应一个上下文 {@link ServiceContext}
     *        - {@link HArk}（方舟），应用容器资源管理器组件
     *          - {@link HApp}（应用），应用组件，底层对应 X_APP 和 app.yml 配置文件实现配置和持久化连接
     *        - {@link ServiceContext}，服务插件上下文
     *          - {@link HSetting} 入口设置信息
     *          - {@link MDConfiguration} 模块配置信息
     * </code></pre>
     * 服务发布清单，格式：接口插件 / 实现插件
     * <pre><code>
     *     核心服务
     *     1. {@link EnergyFailure}
     *         Zero.Core.Runtime.KMetadata / Zero.Core.Runtime.KMetadata
     *     2. {@link EnergyService}
     *         Zero.Core.Runtime.KMetadata / Zero.Core.Runtime.KMetadata
     *     3. {@link EnergyConfiguration}
     *         Zero.Core.Runtime.KMetadata / Zero.Core.Web.Model
     *     4. {@link EnergyDeployment}
     *         Zero.Core.Runtime.KMetadata / Zero.Core.Web.Container
     *     其他可用服务
     *     5. {@see EnergyVertx}
     *         Zero.Core.Web.Container / Zero.Core.Web.Container
     * </code></pre>
     *
     * @param parameter 回调参数
     * @param okRefs    条件链
     */
    public CallbackOfService(final CallbackParameter parameter,
                             final OOnce<?>... okRefs) {
        this.parameter = parameter;
        this.onceService = this.ofOnceService();
        // 追加配置
        this.okRefs.add(new OnceConfiguration());
        this.okRefs.add(new OnceDeployment());
        this.okRefs.addAll(Arrays.asList(okRefs));
    }

    // ------------ 子类专用方法

    protected OOnce<EnergyService> ofOnceService() {
        return new OnceService();
    }

    protected CallbackParameter ofParameter() {
        return this.parameter;
    }

    protected Class<?> startFinished(final Class<?> interfaceCls, final Supplier<Class<?>> supplierImplFn) {
        final Class<?> serviceImpl = supplierImplFn.get();
        final DependencyManager manager = this.ofParameter().dependency();
        manager.add(manager.createComponent()
            .setInterface(interfaceCls, null)
            .setImplementation(serviceImpl)
        );
        return serviceImpl;
    }

    // ------------ 接口专用方法
    @Override
    public void start(final Object reference) {
        // OOnce
        this.okRefs.forEach(okFor -> okFor.bind(reference));
        this.onceService.bind(reference);

        // 生命周期子类实现
        this.startBind(reference);

        if (this.isReady()) {
            // 追加配置相关信息
            final ServiceContext context = this.parameter.context();


            // OOnce
            this.okRefs.stream()
                .filter(okFor -> okFor instanceof OOnce.LifeCycle<?>)
                .map(okFor -> (OOnce.LifeCycle<?>) okFor)
                .forEach(okFor -> okFor.start(context));


            this.startService(context);


            // 乱序服务启动流程
            this.startEnd(reference);


            // 服务执行专用流程
            ServiceRunner.of(context.owner()).start(this.parameter);
        }
    }

    private void startService(final ServiceContext context) {
        final EnergyService energy = this.onceService.reference();
        Objects.requireNonNull(energy);

        energy.addContext(context);
        this.parameter.providers().forEach(energy::addProviderService);
        this.parameter.consumers().forEach(service -> energy.addConsumerService(context.owner(), service));
    }

    @Override
    public void stop(final Object reference) {
        // Fix Error: ERROR: [FelixStartLevel] "stop" callback not found on component instances [io.zerows.core.module.dependency.osgi.metadata.CallbackOfService@465e9b70]
        final ServiceContext context = this.parameter.context();
        // 服务执行专用流程
        ServiceRunner.of(context.owner()).stop(this.parameter);

        this.okRefs.stream()
            .filter(okFor -> okFor instanceof OOnce.LifeCycle<?>)
            .map(okFor -> (OOnce.LifeCycle<?>) okFor)
            .forEach(okFor -> okFor.stop(context));

        this.stopService(context);
    }

    private void stopService(final ServiceContext context) {
        final EnergyService energy = this.onceService.reference();
        Objects.requireNonNull(energy);

        energy.removeContext(context);
        this.parameter.providers().forEach(energy::removeProviderService);
        this.parameter.consumers().forEach(service -> energy.removeConsumerService(context.owner(), service));
    }

    protected void startBind(final Object reference) {
        // 子类实现做绑定
    }

    protected void startEnd(final Object reference) {
        // 子类实现，基本执行结束
    }

    @Override
    public synchronized boolean isReady() {
        final boolean isOk;
        if (this.okRefs.isEmpty()) {
            isOk = Boolean.TRUE;
        } else {
            isOk = this.okRefs.stream().allMatch(OOnce::isReady);
        }
        return isOk && this.onceService.isReady();
    }
}
