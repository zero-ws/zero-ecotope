package io.zerows.sdk.plugins;

import io.r2mo.typed.cc.Cc;
import io.zerows.specification.configuration.HConfig;

import java.util.function.Function;

/**
 * Manager 的父类，维护专用，此处 {@link DI} 是被管理的对象，也是最终注入的对象
 *
 * @author lang : 2025-10-16
 */
public abstract class AddOnManager<DI> {

    protected AddOnManager() {
    }

    protected abstract Cc<String, DI> stored();

    @SuppressWarnings("unchecked")
    public <T extends AddOnManager<DI>> T put(final String name, final DI instance) {
        this.stored().put(name, instance);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends AddOnManager<DI>> T remove(final String name) {
        this.stored().remove(name);
        return (T) this;
    }

    public DI get(final String name) {
        return this.stored().get(name);
    }

    public DI get(final String name, final Function<String, DI> constructorFn) {
        return this.stored().pick(() -> constructorFn.apply(name), name);
    }

    public void configure(final HConfig config) {
        // 特殊配置流程（启动时执行）
    }
}
