package io.zerows.boot.full.base;

import io.zerows.extension.module.mbsecore.api.AspectPlugin;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.platform.metadata.KFabric;

import java.util.Objects;

/*
 * 抽象层的 Aspect，用于处理配置
 */
public abstract class AbstractAspect implements AspectPlugin {
    protected transient DataAtom atom;
    protected transient PluginQueue queue;

    @Override
    public AspectPlugin bind(final DataAtom atom) {
        this.atom = atom;
        this.queue = new PluginQueue(atom);
        return this;
    }

    @Override
    public AspectPlugin bind(final KFabric fabric) {
        if (Objects.nonNull(this.queue)) {
            this.queue.bind(fabric);
        }
        return this;
    }
}
