package io.zerows.epoch.corpus.assembly.osgi.dependency;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.assembly.osgi.service.EnergyClass;
import io.zerows.epoch.corpus.metadata.zdk.dependency.OOnce;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceContext;
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
