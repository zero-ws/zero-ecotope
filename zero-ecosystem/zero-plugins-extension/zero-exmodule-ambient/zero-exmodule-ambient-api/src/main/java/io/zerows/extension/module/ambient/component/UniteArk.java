package io.zerows.extension.module.ambient.component;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.specification.app.HArk;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 组合器，直接根据 XApp / XSource 组合器进行组合，根据不同类型组合 {@link HArk} 相关信息
 *
 * @author lang : 2024-07-08
 */
public interface UniteArk<T> {

    Cc<Integer, UniteArk<?>> CC_SKELETON = Cc.open();

    @SuppressWarnings("unchecked")
    static <T> UniteArk<T> of(final Supplier<UniteArk<?>> supplier) {
        return (UniteArk<T>) CC_SKELETON.pick(supplier, supplier.hashCode());
    }

    HArk compile(XApp app, T child);

    default Set<HArk> compile(final ConcurrentMap<String, XApp> apps, final ConcurrentMap<String, T> children) {
        final Set<HArk> arkSet = new LinkedHashSet<>();
        apps.keySet().forEach(appId -> {
            final XApp app = apps.get(appId);
            final T child = children.get(appId);
            arkSet.add(this.compile(app, child));
        });
        return arkSet;
    }
}
