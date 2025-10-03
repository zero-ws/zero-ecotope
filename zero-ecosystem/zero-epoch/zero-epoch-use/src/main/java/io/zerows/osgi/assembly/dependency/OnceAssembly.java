package io.zerows.osgi.assembly.dependency;

import io.zerows.osgi.assembly.service.EnergyClass;
import io.zerows.support.Ut;
import io.zerows.epoch.sdk.osgi.OOnce;
import io.zerows.epoch.sdk.osgi.ServiceContext;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-02
 */
public class OnceAssembly implements OOnce.LifeCycle<EnergyClass> {
    private /* 扫描管理器 */ EnergyClass cachedClass;

    @Override
    public void bind(final Object reference) {
        if (reference instanceof final EnergyClass classes) {
            this.cachedClass = classes;
        }
    }

    @Override
    public boolean isReady() {
        return Ut.isNotNull(this.reference());
    }

    @Override
    public <R> R start(final ServiceContext context) {
        Objects.requireNonNull(context.owner());
        final Bundle bundle = context.owner();
        // 安装
        this.reference().install(bundle);
        return null;
    }

    @Override
    public void stop(final ServiceContext context) {
        Objects.requireNonNull(context.owner());
        // 卸载
        this.reference().uninstall(context.owner());
    }

    @Override
    public EnergyClass reference() {
        return this.cachedClass;
    }
}
