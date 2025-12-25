package io.zerows.extension.crud.boot;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.extension.crud.common.IxConfig;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2025-12-24
 */
public interface IxSetup<T> {

    Cc<String, IxSetup<?>> CC_SKELETON = Cc.open();

    @SuppressWarnings("unchecked")
    static <R> IxSetup<R> of(final Function<IxConfig, IxSetup<R>> constructorFn, final IxConfig config) {
        final String keyCached = config.hashCode() + "@" + constructorFn.hashCode();
        return (IxSetup<R>) CC_SKELETON.pick(() -> constructorFn.apply(config), keyCached);
    }

    Boolean configure(Set<MDConfiguration> waitSet);

    ConcurrentMap<String, T> map();

    T map(String key);
}
