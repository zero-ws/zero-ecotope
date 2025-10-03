package io.zerows.epoch.sdk.osgi;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.constant.KName;
import io.zerows.osgi.metadata.dependency.CallbackOfBase;
import io.zerows.osgi.metadata.service.EnergyFailure;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.osgi.framework.Bundle;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 功能
 * <pre><code>
 *     * 异常依赖服务：
 *     - 等待异常服务启动之后会触发当前 Bundle 的异常扫描，将扫描的异常结果存储在异常管理器中，合计会包含
 *       - 配置文件中的异常（只读、默认模式）
 *       - 内存中的异常（可管理，start 时安装、stop 时卸载），可分 Bundle
 * </code></pre>
 *
 * @author lang : 2024-03-28
 */
public abstract class AbstractConnectorBase implements ServiceConnector {
    protected static Cc<String, ServiceConnector> CCT_REFS = Cc.open();
    protected Bundle bundle;

    protected AbstractConnectorBase(final Bundle bundle) {
        this.bundle = bundle;
    }

    protected static ServiceConnector of(final Bundle bundle, final Function<Bundle, ServiceConnector> constructor) {
        final String key = constructor.apply(bundle).getClass().getName() + bundle.hashCode();
        return CCT_REFS.pick(() -> constructor.apply(bundle), key);
    }

    @Override
    public void serviceDependency(final DependencyManager dm,
                                  final Supplier<Component> supplier,
                                  final Supplier<ServiceDependency> serviceSupplier) {
        dm.add(supplier.get().setImplementation(new CallbackOfBase(this.bundle))
            .add(serviceSupplier.get().setService(EnergyFailure.class)
                .setRequired(Boolean.TRUE).setCallbacks(KName.START, KName.STOP)
            )
        );
    }

    @Override
    public void serviceRegister(final DependencyManager dm, final Supplier<Component> supplier) {

    }
}
